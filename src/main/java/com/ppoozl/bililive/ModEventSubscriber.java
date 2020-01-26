package com.ppoozl.bililive;

import com.ppoozl.bililive.api.event.*;
import com.ppoozl.bililive.api.thread.BiliLiveThreadFactory;
import com.ppoozl.bililive.command.BiliLiveCommand;
import com.ppoozl.bililive.config.BiliLiveConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ModEventSubscriber {

    private static int tmpPopularityCount = 0;

    @SubscribeEvent
    public static void onClientChat(ClientChatEvent evt){
        final String command = evt.getMessage();
        String arg;
        BiliLive.player = Minecraft.getInstance().player;
        if(command.equals("/bilidm")) {
            BiliLiveCommand.bilidmAction("help", Minecraft.getInstance().player);
            Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(command);
            evt.setCanceled(true);
        }
        if(command.startsWith( "/bilidm " )){
            arg = command.substring(8);
            BiliLiveCommand.bilidmAction(arg , Minecraft.getInstance().player);
            Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(command);
            evt.setCanceled(true);
        }

    }

    // 玩家进入服务器时，依据配置提供的平台，启动对于的弹幕线程
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent evt) {
        BiliLive.player = evt.getPlayer();
    }

    // 当玩家离开游戏时，停止所有线程
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent evt) {
        BiliLive.player = null;
        BiliLiveThreadFactory.stopAllThreads();
    }

    /**
     * 发送普通弹幕
     *
     * @param e 发送弹幕事件
     */
    @SubscribeEvent
    public static void receiveDanmu(BiliLiveEvent e) {
            PlayerEntity player = Minecraft.getInstance().player;
            Pattern readDanmuUser = Pattern.compile(BiliLiveConfig.blockPlayer.get()); // 屏蔽弹幕发送者
            Pattern readDanmuuMsg = Pattern.compile(BiliLiveConfig.blockDanmu.get()); // 屏蔽弹幕关键词

            // 先判定是否开启弹幕显示
            if (player != null && BiliLiveConfig.showDanmu.get()) {
                // 获取弹幕信息
                String msg = e.getMsg();

                // 进行格式符消除，关键词屏蔽
                if (BiliLiveConfig.blockFormatCode.get()) {
                    // 替换消除格式符，更有效的祛除格式符
                    msg = e.getMsg().replaceAll("(?:§(?=§*(\\1?+[0-9a-fk-or])))+\\1", "");

                    // 如果不为空，消除关键词
                    if (!BiliLiveConfig.blockKeyword.get().isEmpty())
                        msg = msg.replaceAll(BiliLiveConfig.blockKeyword.get(), "*");
                }

                // 进行指定玩家屏蔽，指定弹幕屏蔽
                Matcher mUser = readDanmuUser.matcher(e.getUser());
                Matcher mMsg = readDanmuuMsg.matcher(msg);
                // 没找到，或者对应列表为空
                if ((!mUser.find() || BiliLiveConfig.blockPlayer.get().isEmpty())
                        && (!mMsg.find() || BiliLiveConfig.blockDanmu.get().isEmpty())) {
                    player.sendMessage(new StringTextComponent(String.format(BiliLiveConfig.danmuStyle.get(),
                            e.getPlatform(), e.getUser(), msg)));
                }
            }
        }

        /**
         * 发送礼物
         *
         * @param e 发送礼物事件
         */
        @SubscribeEvent
        public static void receiveGift(GiftEvent e) {
            PlayerEntity player = Minecraft.getInstance().player;
            Pattern readGiftName = Pattern.compile(BiliLiveConfig.blockGift.get()); // 屏蔽礼物

            // 先判定是否开启礼物显示
            if (player != null && (BiliLiveConfig.showGift.get())) {
                // 进行礼物屏蔽
                Matcher mGift = readGiftName.matcher(e.getGiftName());
                // 没找到，或者对应列表为空
                if (!mGift.find() || (BiliLiveConfig.blockGift.get().isEmpty())) {
                    player.sendMessage(new StringTextComponent(String.format(BiliLiveConfig.giftStyle.get(),
                            e.getPlatform(), e.getUser(), e.getGiftName(), e.getNum())));
                }
            }
        }

        /**
         * 欢迎玩家进入
         *
         * @param e 玩家进入事件
         */
        @SubscribeEvent
        public static void welcome(WelcomeEvent e) {
            PlayerEntity player = Minecraft.getInstance().player;
            Pattern readWelcome = Pattern.compile(BiliLiveConfig.blockWelcome.get()); // 屏蔽欢迎玩家

            // 先判定是否显示欢迎信息
            if (player != null && BiliLiveConfig.showWelcome.get()) {
                // 进行玩家屏蔽
                Matcher mWelcome = readWelcome.matcher(e.getUser());
                // 没找到，或者对应列表为空
                if (!mWelcome.find() || BiliLiveConfig.blockWelcome.get().isEmpty()) {
                    player.sendMessage(new StringTextComponent(String.format(BiliLiveConfig.welcomeStyle.get(),
                            e.getPlatform(), e.getUser())));
                }
            }
        }

        /**
         * 获取人气值
         *
         * @param e 得到人气值事件
         */
        @SubscribeEvent
        public static void getPopularityCount(PopularityEvent e) {
            tmpPopularityCount = e.getPopularity();
        }

        @SubscribeEvent
        public static void showPopularityCount(RenderGameOverlayEvent.Post e) {
            IngameGui gui = Minecraft.getInstance().ingameGUI; // 获取 Minecraft 实例中的 GUI
            FontRenderer renderer = Minecraft.getInstance().fontRenderer; // 获取 Minecraft 原版字体渲染器

            // 当渲染快捷栏时候进行显示，意味着 F1 会隐藏
            if (e.getType() == RenderGameOverlayEvent.ElementType.HOTBAR
                    && gui != null && BiliLiveConfig.showPopularity.get()
            && Minecraft.getInstance().currentScreen != null
            ) {
                double x = (Minecraft.getInstance().currentScreen.width * BiliLiveConfig.posX.get()) / 100; // 获取的配置宽度百分比
                double y = (Minecraft.getInstance().currentScreen.height * BiliLiveConfig.posY.get()) / 100; // 获取的配置高度百分比

                gui.drawString(renderer, String.format(BiliLiveConfig.popularityStyle.get(), tmpPopularityCount),
                        (int) x, (int) y, BiliLiveConfig.color.get());
            }
        }
}
