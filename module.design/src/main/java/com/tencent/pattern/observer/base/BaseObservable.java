package com.tencent.pattern.observer.base;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by rubinqiu on 2017/1/16.
 * 被观察者抽象类，业务可以继承此类实现观察者模式，
 * 这里要求:观察者必须实现IObserver
 */

public abstract class BaseObservable<O extends IObserver<D>,D extends IBaseBean> implements IObservable<O,D> {
    private static final String TAG = BaseObservable.class.getSimpleName();

    private volatile List<O> mObserverList;
    private final ReadWriteLock mLock;

    protected BaseObservable(){
        mObserverList = new ArrayList<>();
        mLock = new ReentrantReadWriteLock();
    }

    @Override
    public void register(@NonNull O observer) {
        mLock.writeLock().lock();
        mObserverList.add(observer);
        mLock.writeLock().unlock();
    }

    @Override
    public void unRegister(@NonNull O observer) {
        mLock.writeLock().lock();
        mObserverList.remove(observer);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(D data) {
        if (data == null) {
            Log.e(TAG,"data is null,update error!");
            return;
        }
        mLock.readLock().lock();
        if(mObserverList.size() > 0){
            for(O observer:mObserverList){
                observer.onUpdate(data);
            }
        }
        mLock.readLock().unlock();
    }

    @Override
    public int getObservers() {
        return mObserverList.size();
    }

    @Override
    public void clear() {
        mObserverList.clear();
    }
}
