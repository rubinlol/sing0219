package com.tencent.pattern.singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by rubinqiu on 2017/1/17.
 * 单例工厂
 */

public class SingletonFactory {
    private static SingleTon sSingleTon;
    static {
        try {
            Class cl = Class.forName(SingleTon.class.getName());
            Constructor constructor = cl.getDeclaredConstructor();
            constructor.setAccessible(true);
            try {
                sSingleTon = (SingleTon)constructor.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static SingleTon getInstance(){
        return sSingleTon;
    }

}
