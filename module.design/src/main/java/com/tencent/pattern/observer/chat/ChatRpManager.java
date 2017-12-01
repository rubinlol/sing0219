package com.tencent.pattern.observer.chat;


import com.tencent.pattern.observer.IRpMsgType;
import com.tencent.pattern.observer.base.BaseObservable;

/**
 * Created by rubinqiu on 2017/1/16.
 *
 */

public class ChatRpManager extends BaseObservable<IRpChatObserver,ChatRpRpBean> implements IRpMsgType {
    private static final String TAG = ChatRpManager.class.getSimpleName();

    public static ChatRpManager newInstance(){
        return new ChatRpManager();
    }

    private ChatRpManager(){
        super();
    }

    @Override
    public MsgType getUnReadMsgType() {
        return MsgType.RP_TYPE_NONE;//如果有新的聊天消息，返回RP_TYPE_CHAT;否则返回RP_TYPE_NONE
    }
}
