/**
 * Name : SimpleClock.java<br>
 * Copyright : Copyright (c) Tencent Inc. All rights reserved.<br>
 * Description : Simple Clock Service for less Thread<br>
 */
package com.tencent.appframework.clock;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/**
 * 简单的定时器服务<br>
 * <br>
 * 利用{@code android.os.Handler}实现消息循环减少线程的消耗，但精度会有微量的损失，大约10ms级别<br>
 * <br>
 * 使用{@code SimpleClock.set()}方法来添加一个定时器 <br>
 * 使用{@code SimpleClock.cancel()}方法来取消一个定时器 <br>
 * <br>
 * 默认最多支持32个定时器，超过限制后会返回null<br>
 *
 * @author lewistian
 */
public class SimpleClock extends Clock {
    private static final String CLOCK_SERVICE_NAME = "base.clock.service";
    private static final int CLOCK_MAX_COUNT = 32;

    private static SimpleClock[] clocks;
    private static HandlerThread clockThread;
    private static Handler clockHandler;
    private volatile boolean canceled;

    public static SimpleClock set(long interval, long delay, OnClockListener listener) {
        synchronized (SimpleClock.class) {
            SimpleClock.initClockService();

            int id = -1;

            // 找到空闲的闹钟位置
            for (int i = 0; i < clocks.length; i++) {
                if (clocks[i] == null) {
                    id = i;
                    break;
                }
            }

            // 没有位置则创建闹钟失败
            if (id < 0) {
                return null;
            }

            // 创建闹钟
            SimpleClock clock = new SimpleClock(id, interval, listener);
            clocks[id] = clock;

            prepareNextInterval(id, delay);

            return clock;
        }
    }

    public static void cancel(SimpleClock clock) {
        if (clock == null) {
            return;
        }

        clock.setCanceled();

        final int clockId = clock.getClockId();

        synchronized (SimpleClock.class) {
            if (clockId < 0 || clockId >= clocks.length) {
                return;
            }

            SimpleClock theClock = clocks[clockId];

            if ((theClock != null) && (theClock == clock)) {
                clocks[clockId] = null;
                clockHandler.removeMessages(clockId);
            }
        }
    }

    private static void initClockService() {
        synchronized (SimpleClock.class) {
            if (clocks == null) {
                clocks = new SimpleClock[CLOCK_MAX_COUNT];
            }

            if (clockThread == null) {
                clockThread = new HandlerThread(CLOCK_SERVICE_NAME);
            }

            if (!clockThread.isAlive()) {
                clockThread.start();
            }

            if (clockThread.isAlive()) {
                if (clockHandler == null) {
                    clockHandler = new Handler(clockThread.getLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            handleClockMessage(msg.what);
                        }
                    };
                }
            }
        }
    }

    private static synchronized void handleClockMessage(int clockId) {
        if (clockId < 0 || clockId >= clocks.length) {
            return;
        }

        SimpleClock clock = clocks[clockId];

        if (clock != null) {
            OnClockListener listener = clock.getListener();

            if (listener != null) {
                boolean proceed = listener.onClockArrived(clock);

                if (proceed) {
                    prepareNextInterval(clockId, clock.getInterval());
                } else {
                    cancel(clock);
                }
            }
        }
    }

    private static void prepareNextInterval(int clockId, long delay) {
        if (clockHandler != null) {
            if (delay > 0) {
                clockHandler.sendEmptyMessageDelayed(clockId, delay);
            } else {
                clockHandler.sendEmptyMessage(clockId);
            }
        }
    }

    protected SimpleClock(int clockId, long interval, OnClockListener listener) {
        super(clockId, interval, listener);
    }

    @Override
    public void cancel() {
        SimpleClock.cancel(this);
    }

    public synchronized boolean isCanceled() {
        return canceled;
    }

    public synchronized void setCanceled() {
        canceled = true;
    }
}
