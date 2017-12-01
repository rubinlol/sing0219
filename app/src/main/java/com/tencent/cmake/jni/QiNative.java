package com.tencent.cmake.jni;

import android.util.Log;

/**
 * jni接口
 */
public class QiNative {
    private static final String TAG = "QiNative";

    static {
        System.loadLibrary("demo");
        System.loadLibrary("native-lib");
        init();
    }

    public native static void init();
    public native String getStringFromJni();
    public native int getIntFromJni();
    public native void setPerson(Person person);
    public native Person getPersonFromJni();

    //----------------以下是Jni回调给Java的方法-------分割线-------------//

    public void methodCalledByJni(String msgFromJni){
        Log.d(TAG,"methodCalledByJni ,msg =" + msgFromJni);
    }

    public static void staticMethodCalledByJni(String msgFromJni){
        Log.d(TAG,"staticMethodCalledByJni ,msg =" + msgFromJni + ",threadName=" + Thread.currentThread().getName());
    }
}