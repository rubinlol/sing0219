package com.tencent.cmake;

import android.app.Application;

import com.tencent.appframework.init.ComponentContext;

/**
 * Created by rubinqiu on 2017/11/30.
 *
 */
public class CmakeApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ComponentContext.setContext(this);
    }
}
