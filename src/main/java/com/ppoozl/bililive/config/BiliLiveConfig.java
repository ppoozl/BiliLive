package com.ppoozl.bililive.config;

import com.ppoozl.bililive.BiliLive;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BiliLiveConfig {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec SPEC;

        public static final ForgeConfigSpec.BooleanValue showPopularity;
        public static final ForgeConfigSpec.ConfigValue<String> popularityStyle;
        public static final ForgeConfigSpec.DoubleValue posX;
        public static final ForgeConfigSpec.DoubleValue posY;
        public static final ForgeConfigSpec.IntValue color;

        public static final ForgeConfigSpec.BooleanValue showDanmu;
        public static final ForgeConfigSpec.ConfigValue<String> danmuStyle;
        public static final ForgeConfigSpec.BooleanValue showGift;
        public static final ForgeConfigSpec.ConfigValue<String> giftStyle;
        public static final ForgeConfigSpec.BooleanValue showWelcome;
        public static final ForgeConfigSpec.ConfigValue<String> welcomeStyle ;

        public static final ForgeConfigSpec.ConfigValue<String> platform;
        public static final ForgeConfigSpec.IntValue room;
        public static final ForgeConfigSpec.ConfigValue<String> displayName;

        public static final ForgeConfigSpec.BooleanValue blockFormatCode;
        public static final ForgeConfigSpec.ConfigValue<String> blockPlayer;
        public static final ForgeConfigSpec.ConfigValue<String> blockDanmu;
        public static final ForgeConfigSpec.ConfigValue<String> blockKeyword;
        public static final ForgeConfigSpec.ConfigValue<String> blockGift;
        public static final ForgeConfigSpec.ConfigValue<String> blockWelcome;

        public static final ForgeConfigSpec.IntValue timeout;
        public static final ForgeConfigSpec.IntValue retry;
        public static final ForgeConfigSpec.IntValue retryInterval;

        static
        {
            BUILDER.comment("Bililive config file.");
            BUILDER.push("generals");
            showPopularity = BUILDER
                    .comment("是否显示人气值")
                    .define("showPopularity", true);
            popularityStyle = BUILDER
                    .comment("人气值格式")
                    .define("popularityStyle", "§6§l人气值: %1$s");
            posX = BUILDER
                    .comment("人气值位置横向百分比")
                    .defineInRange("posX", .618d,0d,1d);
            posY = BUILDER
                    .comment("人气值位置纵向百分比")
                    .defineInRange("posY", 1d,0d,1d);
            color = BUILDER
                    .comment("人气值文字阴影颜色")
                    .defineInRange("color" , 0xffffff,0x0,0xffffff);
            BUILDER.pop();

            BUILDER.push("ChatMsg");
                showDanmu = BUILDER
                        .comment("是否显示弹幕")
                        .define("showDanmu" , true);
                danmuStyle = BUILDER
                    .comment("弹幕格式")
                    .define("danmuStyle" , "§7§l[§8§l%1$s§7§l] §6§l%2$s: §f§l%3$s");
                showGift = BUILDER
                    .comment("是否显示礼物")
                    .define("showGift" , true);
                giftStyle = BUILDER
                    .comment("礼物格式")
                    .define("giftStyle" , "§7§l[§8§l%1$s§7§l] §8§l%2$s: %3$sx%4$d");
                showWelcome = BUILDER
                    .comment("是否显示欢迎信息")
                    .define("showWelcome" , true);
                welcomeStyle = BUILDER
                    .comment("欢迎信息格式")
                    .define("welcomeStyle" ,"§7§l[§8§l%1$s§7§l] §f§l欢迎 §6§l%2$s§f§l 加入直播间");
            BUILDER.pop();

            BUILDER.push("LivePlatform");
                platform = BUILDER
                        .comment("直播平台选择，可填 bilibili 和 douyu 以及 chushou")
                        .define("platform" , "bilibili");
                room= BUILDER
                        .comment("直播间房间号")
                        .defineInRange("room" , 0 , 0 , Integer.MAX_VALUE);
                displayName= BUILDER
                        .comment("直播平台名称")
                        .define("displayName" , "哔哩哔哩");
            BUILDER.pop();

            BUILDER.push("BlockFunction");
                blockFormatCode = BUILDER
                    .comment("是否屏蔽样式代码")
                    .define("blockFormatCode" , false);
                blockPlayer = BUILDER
                    .comment("屏蔽用户名")
                    .define("blockPlayer" , "");
                blockDanmu = BUILDER
                        .comment("屏蔽弹幕关键词")
                        .define("blockDanmu" , "");
                blockKeyword = BUILDER
                .comment("消除弹幕关键词")
                .define("blockKeyword" , "");
                blockGift = BUILDER
                .comment("屏蔽礼物")
                .define("blockGift" , "");
                blockWelcome = BUILDER
                .comment("屏蔽观众欢迎")
                .define("blockWelcome" , "");
            BUILDER.pop();

            BUILDER.push("Network");
                timeout = BUILDER
                        .comment("超时时间")
                        .defineInRange("timeout" , 2000 , 0 , 20000);
                retry = BUILDER
                    .comment("重连次数")
                    .defineInRange("retry" , 3 , 0 , 10);
                retryInterval = BUILDER
                        .comment("超时时间")
                        .defineInRange("retryInterval" , 2000 , 0 , 20000);
            BUILDER.pop();

            SPEC = BUILDER.build();

        }


    public static void setup()
    {
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path bililiveConfigPath = Paths.get(configPath.toAbsolutePath().toString());

        try
        {
            Files.createDirectory(bililiveConfigPath);
        }
        catch (FileAlreadyExistsException e)
        {
            // Do nothing
        }
        catch (IOException e)
        {
            BiliLive.logger.error("Failed to create bililive config directory", e);
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC, "bililive.toml");
    }

}
