package com.tencent.pattern.observer.notify;


import com.tencent.pattern.observer.IRpMsgType;
import com.tencent.pattern.observer.base.BaseObservable;

/**
 * Created by rubinqiu on 2017/1/16.
 *
 */

public class NotifyRpManager extends BaseObservable<IRpNotifyObserver,NotifyRpBean> implements IRpMsgType {
    private static final String TAG = NotifyRpManager.class.getSimpleName();

    public static NotifyRpManager newInstance(){
        return new NotifyRpManager();
    }

    private NotifyRpManager(){
        super();
    }

    @Override
    public MsgType getUnReadMsgType() {
        return MsgType.RP_TYPE_NONE;//如果有新的通知消息，返回RP_TYPE_NOTIFY;否则返回RP_TYPE_NONE
    }
}
