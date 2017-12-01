/**
 * Name : OnClockListener.java<br>
 * Copyright : Copyright (c) Tencent Inc. All rights reserved.<br>
 * Description : Clock Arrived Event Handler<br>
 */
package com.tencent.appframework.clock;


/**
 * 定时事件监听器 <br>
 * <br>
 * 实现这个接口，并设定给指定的{@link Clock}对象
 *
 * @author lewistian
 */
public interface OnClockListener {
    /**
     * 当定时器时间到达时触发该事件
     *
     * @param sender 触发事件的定时器对象
     * @return 若要持续该定时器，返回true；<br>
     * 若要取消该定时器，返回false
     */
    public boolean onClockArrived(Clock sender);
}
