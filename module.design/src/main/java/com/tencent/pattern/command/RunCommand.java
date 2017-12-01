package com.tencent.pattern.command;

import android.util.Log;

/**
 * Created by rubinqiu on 2017/3/16.
 * 跑步命令
 */

public class RunCommand implements ICommand{

    private IReceiver mReceiver;

    public RunCommand(IReceiver receiver){
        mReceiver = receiver;
    }

    @Override
    public void execute() {
        if(mReceiver != null)mReceiver.act();
        Log.d(TAG,"warrior run in playground...");
    }
}
