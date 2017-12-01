package com.tencent.pattern.factory;

/**
 * Created by rubinqiu on 2017/1/17.
 * 工厂方法
 */
public abstract class AbstratMsgTypeFactory {
    public abstract <T extends IMsgType> T createMsgType(Class<T> c);
}
