package com.tencent.pattern.singleton;

/**
 * Created by rubinqiu on 2016/6/30.
 * 懒汉式，双重检查锁，(double checked locking pattern)
 */
public class DbcSingleton {

    /**
     * volatile的作用：
     * 1.这个变量不会在多个线程中存在复本，直接从内存读取。
     * 2.这个关键字会禁止指令重排序优化
     * 3.缺点：不具备原子性
     */
    private volatile static DbcSingleton instance;

    private DbcSingleton() {
        // 也可以在这里使用 flag 或计数器 count 来判断
        if (null != instance) {
            throw new RuntimeException("Cannot construct a Singleton more than once!");//防止反射
        }
    }

    public static DbcSingleton getInstance() {
        if (instance == null) {
            synchronized (DbcSingleton.class) {
                if (instance == null) {
                    instance = new DbcSingleton();    //这语句并非原子操作
                }
            }
        }
        return instance;
    }
}

/**
 * JVM针对instance = new Singleton_four();做了以下事情
 * 1.给 Singleton_four 分配内存
 * 2.调用 Singleton_four 的构造函数来初始化成员变量
 * 3.将instance引用指向分配的内存空间（执行完这步 instance 就为非 null 了）
 * <p/>
 * JVM 的即时编译器中存在指令重排的优化，1-2-3/1-3-2不能保证，如果是1-3-2会出现CPU可能被其他线程枪，导致第一个检查为非null。
 * （一个CPU只允许一个线程在运行，而锁是不能被抢占的只能等待...）
 * <p/>
 * 使用volatile的作用：
 * 1.这个变量不会在多个线程中存在复本，直接从内存读取。
 * 2.这个关键字会禁止指令重排序优化
 * 使用volatile后，取操作必须在执行完 1-2-3 之后或者 1-3-2 之后，不存在执行到 1-3 然后取到值的情况。
 */