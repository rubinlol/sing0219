package com.tencent.pattern.observer.base;

/**
 * Created by rubinqiu on 2017/1/16.
 * 被观察者接口
 */

public interface IObservable<O,D> {
    void register(O observer);
    void unRegister(O observer);
    void update(D data);
    int getObservers();
    void clear();
}
