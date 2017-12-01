package com.tencent.pattern.singleton;

/**
 * Created by rubinqiu on 2016/12/27
 * 枚举类型单例,不能反射(会抛出“ Exception in thread "main" java.lang.NoSuchMethodException: net.local.singleton.EnumSingleton.<init>() ”异常)，
 * 也没有反序列化问题
 */
public enum EnumSingleton {
    SINGLE;

    EnumSingleton() {
    }

    public static EnumSingleton getInstance() {
        return SINGLE;
    }

    public String getId(){
        return "TX-0001";
    }
}
