package com.tencent.pattern.command;

/**
 * Created by rubinqiu on 2017/3/16.
 * 命令实施者
 */

public class Invoker {
    private ICommand mICommand;

    public Invoker(ICommand command){
        this.mICommand = command;
    }

    public void setCommand(ICommand iCommand){
        mICommand = iCommand;
    }

    public void execute(){
        if(mICommand != null){
            mICommand.execute();
        }
    }
}
