package com.tencent.pattern.singleton;

/**
 * Created by rubinqiu on 2016/6/30.
 * 饿汉式,Singleton实例的创建是依赖参数或者配置文件的，这个时候饿汉式就无法使用，在 newInstance() 之前必须调用某个方法设置参数给它
 */
public class EagerSingleton {
    //类加载时就初始化
    private static final EagerSingleton instance = new EagerSingleton();

    private EagerSingleton(){}

    public static EagerSingleton getInstance(){
        return instance;
    }
}
