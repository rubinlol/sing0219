package com.tencent.pattern.command;

import android.util.Log;

/**
 * Created by rubinqiu on 2017/3/16.
 * 跳伞命令
 */

public class ParachuteCommand implements ICommand{
    private IReceiver mReceiver;

    public ParachuteCommand(IReceiver receiver){
        mReceiver = receiver;
    }

    @Override
    public void execute() {
        if(mReceiver != null)mReceiver.act();
        Log.d(TAG,"parachute in the sky...");
    }
}
