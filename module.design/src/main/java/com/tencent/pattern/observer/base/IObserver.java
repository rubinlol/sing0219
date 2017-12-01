package com.tencent.pattern.observer.base;

/**
 * Created by rubinqiu on 2017/1/16.
 * 观察者接口
 */

public interface IObserver<D> {
    void onUpdate(D data);
}
