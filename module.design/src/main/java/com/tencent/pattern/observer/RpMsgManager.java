package com.tencent.pattern.observer;

import android.support.annotation.NonNull;
import android.util.Log;

import com.tencent.pattern.observer.base.IBaseBean;
import com.tencent.pattern.observer.chat.ChatRpManager;
import com.tencent.pattern.observer.chat.ChatRpRpBean;
import com.tencent.pattern.observer.chat.IChatObservable;
import com.tencent.pattern.observer.chat.IRpChatObserver;
import com.tencent.pattern.observer.notify.INotifyObservable;
import com.tencent.pattern.observer.notify.IRpNotifyObserver;
import com.tencent.pattern.observer.notify.NotifyRpBean;
import com.tencent.pattern.observer.notify.NotifyRpManager;


/**
 * Created by rubinqiu on 2017/1/16.
 * 使用枚举单例
 */

public enum RpMsgManager implements IChatObservable, INotifyObservable, IRpMsgType {
    SINGLE;
    private static final String TAG = RpMsgManager.class.getSimpleName();

    private NotifyRpManager mNotifyManager;
    private ChatRpManager mChatManager;

    public static RpMsgManager getInstance() {
        return SINGLE;
    }

    private RpMsgManager() {
        mNotifyManager = NotifyRpManager.newInstance();
        mChatManager = ChatRpManager.newInstance();
    }

    public void onChange(@NonNull MsgType msgType, @NonNull IBaseBean data) {
        Log.d(TAG, String.format("type=%d,data=%s", msgType.getType(), data.toString()));
        switch (msgType) {
            case RP_TYPE_CHAT:
                if (data instanceof ChatRpRpBean) {
                    mChatManager.update((ChatRpRpBean) data);
                }
                break;
            case RP_TYPE_NOTIFY:
                if (data instanceof NotifyRpBean) {
                    mNotifyManager.update((NotifyRpBean) data);
                }
                break;
            default:
                Log.d(TAG, "onChange RP type not unsupport");
                break;
        }
    }

    public void clear() {
        mChatManager.clear();
        mNotifyManager.clear();
    }

    @Override
    public MsgType getUnReadMsgType() {
        MsgType result = MsgType.RP_TYPE_NONE;
        if (mChatManager.getUnReadMsgType() == MsgType.RP_TYPE_CHAT
                && mNotifyManager.getUnReadMsgType() == MsgType.RP_TYPE_NOTIFY) {
            result = MsgType.RP_TYPE_BOTH;
        } else if (mChatManager.getUnReadMsgType() == MsgType.RP_TYPE_CHAT) {
            result = MsgType.RP_TYPE_CHAT;
        } else if (mNotifyManager.getUnReadMsgType() == MsgType.RP_TYPE_NOTIFY) {
            result = MsgType.RP_TYPE_NOTIFY;
        }
        return result;
    }

    @Override
    public void registerChatOs(IRpChatObserver observer) {
        mChatManager.register(observer);
    }

    @Override
    public void unRegisterChatOs(IRpChatObserver observer) {
        mChatManager.unRegister(observer);
    }

    @Override
    public void registerNotifyOs(IRpNotifyObserver observer) {
        mNotifyManager.register(observer);
    }

    @Override
    public void unRegisterNotifyOs(IRpNotifyObserver observer) {
        mNotifyManager.unRegister(observer);
    }
}
