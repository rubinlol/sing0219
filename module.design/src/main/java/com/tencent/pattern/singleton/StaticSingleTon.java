package com.tencent.pattern.singleton;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Created by rubinqiu on 2016/6/30.
 * 懒汉式,静态内部类方式，读取实例的时候不会进行同步，没有性能缺陷；也不依赖 JDK 版本,但是还是可以通过反射获取构造
 */
public class StaticSingleTon implements Serializable {
    private static final long SerialVersionUID = -763618247875550322L;//序列化时为了保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性。

    private static class SingletonHolder {//内部类不会因为外围类初始化而初始化
        private static final StaticSingleTon INSTANCE = new StaticSingleTon();
    }

    private StaticSingleTon(){}

    public static final StaticSingleTon getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Object readResolve() throws ObjectStreamException {//保证反序列化可以得到同一个对象,保证不可变性
        return SingletonHolder.INSTANCE;
    }
}
