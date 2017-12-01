package com.tencent.appframework.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.debug.hv.ViewServer;
import com.tencent.appframework.swipeback.app.SwipeBackActivity;


/**
 * Created by rubinqiu on 2017/11/23.
 * 支持Hierarchy View布局
 */

public abstract class BaseActivity extends SwipeBackActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewServer.get(this).addWindow(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }
}
