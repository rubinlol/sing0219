package com.tencent.pattern.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubinqiu on 2017/3/16.
 *
 */
public class CommandClient {
    private List<ICommand> mCommandList;
    private IReceiver iReceiver;

    public CommandClient(){
        mCommandList = new ArrayList<>();
        iReceiver = new Receiver();
        mCommandList.add(new RunCommand(iReceiver));
        mCommandList.add(new ParachuteCommand(iReceiver));
    }

    private void init(){
        if(mCommandList == null || mCommandList.size() == 0)return;

        Invoker invoker = new Invoker(null);
        for(ICommand command:mCommandList){
            invoker.setCommand(command);
            invoker.execute();
        }
    }
}
