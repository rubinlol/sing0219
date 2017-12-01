/**
 * Name : Clock.java<br>
 * Copyright : Copyright (c) Tencent Inc. All rights reserved.<br>
 * Description : Clock Concept in Clock Service<br>
 */
package com.tencent.appframework.clock;


/**
 * 定时器基础类，配合具体的定时服务和子类实现各种定时功能
 *
 * @author lewistian
 */
public abstract class Clock {
    private long interval = 10 * 1000L;
    private int clockId = -1;
    private OnClockListener listener;

    /**
     * 创建一个时钟，但通常这种方法都由定时服务提供，故隐藏
     *
     * @param clockId  定时器序号
     * @param interval 定时间隔，单位: ms
     * @param listener 定时事件监听器
     */
    protected Clock(int clockId, long interval, OnClockListener listener) {
        setInterval(interval);
        setClockId(clockId);
        setListener(listener);
    }

    /**
     * 取消定时器，需要子类实现
     */
    public abstract void cancel();

    /**
     * 获得定时间隔
     *
     * @return 定时间隔，单位: ms
     */
    public long getInterval() {
        return interval;
    }

    /**
     * 设置定时间隔，修改该设置只会在定时前和定时后的下次报时生效
     *
     * @param interval 定时间隔，单位: ms
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * 获得定时器序列，这取决于定时器所归属的定时服务
     *
     * @return 定时器序号
     */
    public int getClockId() {
        return clockId;
    }

    /**
     * 设置定时器序列
     *
     * @param clockId 定时器序列
     */
    private void setClockId(int clockId) {
        this.clockId = clockId;
    }

    /**
     * 获得定时事件监听器
     *
     * @return 定时事件监听器
     */
    public OnClockListener getListener() {
        return listener;
    }

    /**
     * 设置定时事件监听器，修改该设置只会在定时前和定时后的下次报时生效
     *
     * @param listener 定时事件监听器
     */
    private void setListener(OnClockListener listener) {
        this.listener = listener;
    }
}
