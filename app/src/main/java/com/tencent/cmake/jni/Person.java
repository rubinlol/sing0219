package com.tencent.cmake.jni;

/**
 * Created by rubinqiu on 2017/11/22.
 * 测试ndk 对象的传递
 */
public class Person {
    private String name;
    private int age;
    private float height;

    public Person(String name,int age,float height){
        this.name = name;
        this.age = age;
        this.height = height;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", height=" + height +
                '}';
    }
}
