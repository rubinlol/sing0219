
package com.tencent.appframework.thread;


public interface FutureListener<T> {
    public void onFutureBegin(Future<T> future);

    public void onFutureDone(Future<T> future);
}
