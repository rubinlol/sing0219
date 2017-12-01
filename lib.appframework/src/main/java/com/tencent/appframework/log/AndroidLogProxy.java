package com.tencent.appframework.log;

import android.util.Log;

import java.io.File;

/**
 * Created by hugozhong on 2016/12/2.
 */
public class AndroidLogProxy implements LogUtil.LogProxy {

    private boolean enable = true;

    @Override
    public void v(String tag, String msg) {
        if (enable) {
            Log.v(tag, msg);
        }
    }

    @Override
    public void d(String tag, String msg) {
        if (enable) {
            Log.d(tag, msg);
        }
    }

    @Override
    public void i(String tag, String msg) {
        if (enable) {
            Log.i(tag, msg);
        }
    }

    @Override
    public void w(String tag, String msg) {
        if (enable) {
            Log.w(tag, msg);
        }
    }

    @Override
    public void e(String tag, String msg) {
        if (enable) {
            Log.e(tag, msg);
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void setTraceLevel(int level) {

    }

    @Override
    public void setLogcatEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void setFileLogEnable(boolean enable) {

    }

    @Override
    public File getWorkerFolder() {
        return null;
    }

    @Override
    public File getWorkerFolder(long time) {
        return null;
    }
}
