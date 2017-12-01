package com.tencent.pattern.observer;

/**
 * Created by rubinqiu on 2017/1/16.
 *
 */

public interface IRpMsgType {
    enum MsgType {
        RP_TYPE_NONE(0x00), //没有消息
        RP_TYPE_CHAT(0x01), //仅有聊天消息
        RP_TYPE_NOTIFY(0x02),//仅有通知消息
        RP_TYPE_BOTH(0x03);//通知和聊天都有

        private int mType;

        MsgType(int type) {
            this.mType = type;
        }

        public int getType() {
            return mType;
        }
    }

    MsgType getUnReadMsgType();
}
