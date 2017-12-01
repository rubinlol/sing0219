package com.tencent.pattern.observer.notify;


import com.tencent.pattern.observer.base.IBaseBean;

/**
 * Created by rubinqiu on 2017/1/16.
 *
 */

public class NotifyRpBean implements IBaseBean {
    private int newNum;

    public static NotifyRpBean newInstance(int newNum){
        return new NotifyRpBean(newNum);
    }

    private NotifyRpBean(int newNum){
        this.newNum = newNum;
    }

    public int getNewNum() {
        return newNum;
    }

    @Override
    public String toString() {
        return "NotifyRpBean{" +
                "newNum=" + newNum +
                '}';
    }
}