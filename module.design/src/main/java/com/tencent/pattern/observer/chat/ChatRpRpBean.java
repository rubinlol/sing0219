package com.tencent.pattern.observer.chat;


import com.tencent.pattern.observer.base.IBaseBean;

/**
 * Created by rubinqiu on 2017/1/16.
 *
 */

public class ChatRpRpBean implements IBaseBean {
    private int newNum;
    private int totalNum;

    public static ChatRpRpBean newInstance(int totalNum, int newNum){
        return new ChatRpRpBean(totalNum,newNum);
    }

    private ChatRpRpBean(int totalNum, int newNum){
        this.totalNum = totalNum;
        this.newNum = newNum;
    }

    public int getNewNum() {
        return newNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    @Override
    public String toString() {
        return "ChatRpBean{" +
                "newNum=" + newNum +
                ", totalNum=" + totalNum +
                '}';
    }
}