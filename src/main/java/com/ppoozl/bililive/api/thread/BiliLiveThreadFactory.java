package com.ppoozl.bililive.api.thread;

import com.ppoozl.bililive.BiliLive;
import com.ppoozl.bililive.config.BiliLiveConfig;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BiliLiveThreadFactory {
    // 存储弹幕线程的 HashMap
    private static final HashMap<String, BaseBiliLiveThread> biliLiveThreads = new HashMap<>(); // 未启动的弹幕线程
    private static final HashMap<String, Thread> realBiliLiveThreads = new HashMap<>(); // 正在运行的弹幕线程

    /**
     * 向 biliLiveThreads 中注册新的 biliLiveThread 类型
     * 通常在 Mod 的 init 阶段使用
     *
     * @param name   biliLiveThread 的名称
     * @param thread biliLiveThread 的实例
     */
    public static void RegisterBiliLiveThread(String name, BaseBiliLiveThread thread) {
        biliLiveThreads.put(name, thread);
    }

    /**
     * 获得指定平台的 biliLiveThread
     *
     * @param platform 平台名
     * @return biliLiveBaseThreads 实例
     */
    public static BaseBiliLiveThread getBiliLiveThread(String platform) {
        return biliLiveThreads.getOrDefault(platform, null);
    }

    /**
     * 获得当前正在运行的线程
     *
     * @return 当前正在运行的线程
     */
    public static ArrayList<String> getRunningBiliLiveThread() {
        return new ArrayList<>(realBiliLiveThreads.keySet());
    }

    /**
     * 启动指定平台的 biliLiveThread
     *
     * @param platform 平台名
     */
    public static void runThread(String platform) {
        // 先获取当前正在运行的弹幕线程
        BaseBiliLiveThread dmThread = getBiliLiveThread(platform);

        // 如果正在运行的弹幕线程为空
        if (dmThread != null) {
            // 将弹幕开启的指示参数设定为 true
            dmThread.keepRunning = true;

            // 而后重新 new 线程，并启动线程
            Thread threadToRun = new Thread(dmThread, platform + "biliLiveThread");
            threadToRun.start();

            // 在存储实际运行的 map 中存入这个线程
            realBiliLiveThreads.put(platform, threadToRun);
        } else {
            // 发送错误信息
            BiliLive.logger.error("平台 [" + platform + "] 不存在！请检查配置文件或已安装 Mod！");
            BaseBiliLiveThread.sendChatMessage(TextFormatting.RED + "弹幕姬错误：");
            BaseBiliLiveThread.sendChatMessage(TextFormatting.RED + "平台 [" + platform + "] 不存在！请检查配置文件或已安装 Mod！");
        }
    }

    /**
     * 停止指定平台的 biliLiveThread
     *
     * @param platform 平台名
     * @param restart  是否再次启动该线程
     */
    public static void stopThread(String platform, boolean restart) {
        // 先获取当前正在运行的弹幕线程
        BaseBiliLiveThread th = getBiliLiveThread(platform);

        // 如果正在运行的弹幕线程为空
        if (th != null) {
            // 创建新线程，因为关闭弹幕线程是有阻塞的
            new Thread(() -> {
                // 先关闭线程标识符，借此关闭所有弹幕线程
                th.keepRunning = false;

                // 阻塞，等待弹幕线程关闭
                while (realBiliLiveThreads.get(platform).isAlive()) ;

                // 在运行线程表中移除该线程
                realBiliLiveThreads.remove(platform);

                // 清空线程
                th.clear();

                // 如果 restart，则再次启动线程
                if (restart) runThread(platform);
            }, "Stop" + platform + "biliLiveThread").start();
        }
    }

    /**
     * 停止指定平台的 biliLiveThread
     *
     * @param platform 平台名
     */
    public static void stopThread(String platform) {
        stopThread(platform, false);
    }

    /**
     * 停止现在正在运行的所有 biliLiveThread
     * stopThread 会卡住游戏，所以得在单独线程进行关闭操作
     */
    public static void stopAllThreads() {
        realBiliLiveThreads.forEach((platform, thread) -> stopThread(platform));
    }

    /**
     * 判断指定 platform 的 biliLiveThread 是否正在运行
     *
     * @param platform 平台名
     * @return 指定 platform 的 biliLiveThread 是否正在运行
     */
    public static boolean isThreadRunning(String platform) {
        return realBiliLiveThreads.containsKey(platform);
    }

    /**
     * 判断指定 platform 的 biliLiveThread 是否存在
     *
     * @param platform 平台名
     * @return biliLiveThread 存在与否
     */
    public static boolean isbiliLiveThreadAvailable(String platform) {
        return biliLiveThreads.containsKey(platform);
    }

    /**
     * 重启所有线程 同样可以用于初始化时线程的启动
     */
    public static void restartThreads() {
        // 获得所有的 platforms
        String[] _platforms = BiliLiveConfig.platform.get().split(",");
        for (int i = 0; i < _platforms.length; i++) {
            _platforms[i] = _platforms[i].trim(); // 剔除行首行尾空格
        }

        // 获得所有的平台
        ArrayList<String> platforms = new ArrayList<>(Arrays.asList(_platforms));
        // 获得正在运行的弹幕线程
        ArrayList<String> running = getRunningBiliLiveThread();

        // 创建一个 restart 数据，存入刚刚正在运行的弹幕线程列表
        ArrayList<String> restart = new ArrayList<>(running);
        // 获得两者的交集
        restart.retainAll(platforms);

        // 创建一个 toStop 数据，存入刚刚正在运行的弹幕线程列表
        ArrayList<String> toStop = new ArrayList<>(running);
        // 获得两者的差集
        toStop.removeAll(platforms);

        // 创建一个 toStart 数据，存入所有的平台列表
        ArrayList<String> toStart = new ArrayList<>(platforms);
        // 获得两者的差集
        toStart.removeAll((getRunningBiliLiveThread()));

        // restart 部分，依次进行停止、并重启
        restart.forEach((platform) -> stopThread(platform, true));
        // toStop 部分，依次进行停止
        toStop.forEach(BiliLiveThreadFactory::stopThread);
        // toStart 部分，依次进行开启
        toStart.forEach(BiliLiveThreadFactory::runThread);
    }
}
