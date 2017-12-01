
package com.tencent.appframework.log;

import android.util.Log;

import java.io.File;


public class LogUtil {

    public interface LogProxy extends LogLevel {

        public void v(String tag, String msg);

        public void d(String tag, String msg);

        public void i(String tag, String msg);

        public void w(String tag, String msg);

        public void e(String tag, String msg);

        public void flush();

        public void setTraceLevel(int level);

        public void setLogcatEnable(boolean enable);

        public void setFileLogEnable(boolean enable);

        public File getWorkerFolder();

        public File getWorkerFolder(long time);
    }


    private final static LogProxy DEFAULT_PROXY = new AndroidLogProxy();

    private static volatile LogProxy sProxy = DEFAULT_PROXY;

    private static String formatMsg(String msg, Object... args) {
        if (msg == null && args != null && args.length > 0) {
            throw new IllegalArgumentException("msg format is not set but arguments are presented");
        }
        if (msg != null) {
            return String.format(msg, args);
        }
        return "";
    }

    public static void v(String tag, String msg) {
        LogProxy proxy = getProxy();
        proxy.v(tag, msg);
    }

    public static void v(String tag, String msg, Object... args) {
        getProxy().v(tag, formatMsg(msg, args));
    }

    public static void v(String tag, String msg, Throwable tr) {
        LogProxy proxy = getProxy();
        proxy.v(tag, msg + '\n' + getStackTraceString(tr));
    }


    public static void d(String tag, String msg) {
        LogProxy proxy = getProxy();
        proxy.d(tag, msg);
    }

    public static void d(String tag, String msg, Object... args) {
        getProxy().d(tag, formatMsg(msg, args));
    }

    public static void d(String tag, String msg, Throwable tr) {
        LogProxy proxy = getProxy();
        proxy.d(tag, msg + '\n' + getStackTraceString(tr));
    }


    public static void i(String tag, String msg) {
        LogProxy proxy = getProxy();
        proxy.i(tag, msg);
    }

    public static void i(String tag, String msg, Object... args) {
        getProxy().i(tag, formatMsg(msg, args));
    }

    public static void i(String tag, String msg, Throwable tr) {
        LogProxy proxy = getProxy();
        proxy.i(tag, msg + '\n' + getStackTraceString(tr));
    }


    public static void w(String tag, String msg) {
        LogProxy proxy = getProxy();
        proxy.w(tag, msg);
    }

    public static void w(String tag, String msg, Object... args) {
        getProxy().w(tag, formatMsg(msg, args));
    }

    public static void w(String tag, String msg, Throwable tr) {
        LogProxy proxy = getProxy();
        proxy.w(tag, msg + '\n' + getStackTraceString(tr));
    }


    public static void w(String tag, Throwable tr) {
        LogProxy proxy = getProxy();
        proxy.w(tag, getStackTraceString(tr));
    }


    public static void e(String tag, String msg) {
        LogProxy proxy = getProxy();
        proxy.e(tag, msg);
    }

    public static void e(String tag, String msg, Object... args) {
        getProxy().e(tag, formatMsg(msg, args));
    }

    public static void e(String tag, String msg, Throwable tr) {
        LogProxy proxy = getProxy();
        proxy.e(tag, msg + '\n' + getStackTraceString(tr));
    }

    public static void flush() {
        LogProxy proxy = getProxy();
        proxy.flush();
    }

    public static void setTraceLevel(int level) {
        getProxy().setTraceLevel(level);
    }

    public static void setLogcatEnable(boolean enable) {
        getProxy().setLogcatEnable(enable);
    }

    public static void setFileLogEnable(boolean enable) {
        getProxy().setFileLogEnable(enable);
    }

    public static void setProxy(LogProxy proxy) {
        synchronized (LogUtil.class) {
            sProxy = proxy;
        }
    }

    private static LogProxy getProxy() {
        LogProxy proxy = sProxy;
        return proxy != null ? proxy : DEFAULT_PROXY;
    }

    private static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }


    public static File getWorkerFolder() {
        return getProxy().getWorkerFolder();
    }

    public static File getWorkerFolder(long time) {
        return getProxy().getWorkerFolder(time);
    }

}
