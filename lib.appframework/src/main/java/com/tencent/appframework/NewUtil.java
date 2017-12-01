package com.tencent.appframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rubinqiu on 2017/1/6.
 * 用于快捷生成新的容器对象
 */
final public class NewUtil {

    private NewUtil(){}

    public static <T> List<T> arrayList(){
        return new ArrayList<>();
    }

    public static <T> List<T> linkList(){
        return new LinkedList<>();
    }

    public static <T> Set<T> hashSet(){
        return new HashSet<>();
    }

    public static <K,V> Map<K,V> hashMap(){
        return new HashMap<>();
    }
}
