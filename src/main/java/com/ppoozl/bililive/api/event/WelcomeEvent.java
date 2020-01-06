package com.ppoozl.bililive.api.event;

import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class WelcomeEvent extends BaseBiliLiveEvent {
    private String user; // 老爷加入房间，我很想吐槽这个“老爷”，这不是旧社会用语么？

    /**
     * 老爷加入房间的事件
     *
     * @param user 用户名
     */
    public WelcomeEvent(String platform, String user) {
        super(platform);
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
