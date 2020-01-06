package com.ppoozl.bililive.api.event;

import com.ppoozl.bililive.api.event.BaseBiliLiveEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class SysMsgEvent extends BaseBiliLiveEvent {
    private String msg; // 系统信息

    /**
     * 系统信息事件
     *
     * @param msg 系统信息
     */
    public SysMsgEvent(String platform, String msg) {
        super(platform);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
