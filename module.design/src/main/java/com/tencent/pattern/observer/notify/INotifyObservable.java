package com.tencent.pattern.observer.notify;

/**
 * Created by rubinqiu on 2017/1/16.
 *
 */
public interface INotifyObservable {
    void registerNotifyOs(IRpNotifyObserver observer);
    void unRegisterNotifyOs(IRpNotifyObserver observer);
}
