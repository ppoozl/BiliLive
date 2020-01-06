package com.ppoozl.bililive.api.event;

import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class PopularityEvent extends BaseBiliLiveEvent {
    private int popularity; // 人气值

    /**
     * 获取人气值事件
     *
     * @param popularity 人气值
     */
    public PopularityEvent(String platform, int popularity) {
        super(platform);
        this.popularity = popularity;
    }

    public int getPopularity() {
        return popularity;
    }
}
