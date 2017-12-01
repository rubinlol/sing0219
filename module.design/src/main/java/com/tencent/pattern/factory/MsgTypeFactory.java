package com.tencent.pattern.factory;

/**
 * Created by rubinqiu on 2017/1/17.
 *
 */
public class MsgTypeFactory extends AbstratMsgTypeFactory {
    private static final String TAG = MsgTypeFactory.class.getSimpleName();

    public MsgTypeFactory() {
    }

    @Override
    public <T extends IMsgType> T createMsgType(Class<T> c) {
        IMsgType t = null;
        if (null == c) {
            throw new IllegalArgumentException("class is null");
        }
        try {
            t = (IMsgType) Class.forName(c.getName()).newInstance();//要求有默认构造方法,通过反射调用构造方法
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (T)t;
    }
}
