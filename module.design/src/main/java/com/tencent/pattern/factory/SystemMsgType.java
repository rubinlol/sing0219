package com.tencent.pattern.factory;

import android.content.Context;
import android.util.Log;
import android.view.View;

/**
 * Created by rubinqiu on 2017/1/17.
 *
 */

public class SystemMsgType implements IMsgType {
    @Override
    public View getView(Context context) {
        Log.d("SystemMsgType","getView---");
        return null;
    }
}
