package com.ppoozl.bililive.api.event;
import net.minecraftforge.eventbus.api.Event;

public class BaseBiliLiveEvent extends Event{

    private String platform;

    public BaseBiliLiveEvent(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

}
