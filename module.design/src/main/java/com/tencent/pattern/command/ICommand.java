package com.tencent.pattern.command;

/**
 * Created by rubinqiu on 2017/3/16.
 * 这里是模拟军官训练战士的命令模式，ICommand是战士执行命令的接口
 */
public interface ICommand {
    String TAG = ICommand.class.getSimpleName();
    void execute();//执行具体命令
}
