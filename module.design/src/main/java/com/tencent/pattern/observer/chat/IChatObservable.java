package com.tencent.pattern.observer.chat;

/**
 * Created by rubinqiu on 2017/1/16.
 *
 */
public interface IChatObservable {
    void registerChatOs(IRpChatObserver observer);
    void unRegisterChatOs(IRpChatObserver observer);
}
