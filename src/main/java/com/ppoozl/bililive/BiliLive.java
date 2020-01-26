package com.ppoozl.bililive;

import com.ppoozl.bililive.api.thread.BiliLiveThreadFactory;
import com.ppoozl.bililive.config.BiliLiveConfig;
import com.ppoozl.bililive.thread.BilibiliThread;
import com.ppoozl.bililive.thread.ChushouThread;
import com.ppoozl.bililive.thread.DouyuThread;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = BiliLive.MOD_ID)
public class BiliLive {

    public static final String MOD_ID = "bililive";

    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public static BiliLive instance;

    public static PlayerEntity player;

    @OnlyIn(Dist.CLIENT)
    public BiliLive() {
            instance = this;

            BiliLiveThreadFactory.RegisterBiliLiveThread("bilibili", new BilibiliThread());
            BiliLiveThreadFactory.RegisterBiliLiveThread("douyu", new DouyuThread());
            BiliLiveThreadFactory.RegisterBiliLiveThread("chushou", new ChushouThread());


            BiliLiveConfig.setup();

    }

}



