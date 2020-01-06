package com.ppoozl.bililive.api.thread;

import com.ppoozl.bililive.BiliLive;
import com.ppoozl.bililive.config.BiliLiveConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

public abstract class BaseBiliLiveThread implements Runnable {

    protected int retryCounter = BiliLiveConfig.retry.get(); // 配置文件，重试次数

    public volatile boolean keepRunning = true; // 特殊的修饰符 volatile，用来标定是否进行连接

    @Override
    public void run() {
        // 预检查不通过，不执行
        if (!preRunCheck()) return;

        // 执行主体
        doRun();
    }

    /**
     * 预检查，检查玩家是否为空，尝试获取玩家
     *
     * @return 检查是否通过
     */
    public boolean preRunCheck() {
        if (Minecraft.getInstance().player != null)
            BiliLive.player = Minecraft.getInstance().player;
        return true;
    }

    /**
     * 弹幕线程运行主体
     */
    public abstract void doRun();

    /**
     * 重载时的清除方法，用来清除某些可能需要销毁的数据
     */
    public abstract void clear();

    /**
     * 发送信息，进行游戏内提醒
     *
     * @param text 需要发送的信息
     */
    public static void sendChatMessage(String text) {
        if (BiliLive.player != null)
            BiliLive.player.sendMessage(new StringTextComponent(text));
    }

    /**
     * 超时重连的间隔
     */
    protected void waitForRetryInterval() {
        try {
            Thread.sleep(BiliLiveConfig.retryInterval.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
