
package com.tencent.appframework.init;

import android.app.Application;
import android.content.Context;

/**
 * Author: hugozhong Date: 2013-11-18
 */
public class ComponentContext {

    private static Context mAppContext;

    private ComponentContext() {

    }

    public static void setContext(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public static Context getContext() {
        return mAppContext;
    }

    public static Application getApplication() {
        return (Application) mAppContext.getApplicationContext();
    }

    public final static String getPackageName() {
        return getContext().getPackageName();
    }

}
