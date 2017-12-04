package com.tencent.appframework.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.appframework.dialog.LoadingDialog;
import com.tencent.appframework.thread.ThreadPool;

/**
 * Created by rubinqiu on 2017/12/4.
 * 增加公共的操作
 * 1.缓冲动画
 */
public class CommonActivity extends BaseActivity {
    private static final String TAG = "CommonActivity";
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoadingDialog(true);
    }

    public void showLoadingDialog(long antoHideDelay){
        showLoadingDialog();
        ThreadPool.runUITask(new Runnable() {
            @Override
            public void run() {
                hideLoadingDialog();
            }
        },antoHideDelay);
    }

    public void showLoadingDialog(){
        showLoadingUi("");
    }

    public void hideLoadingDialog(){
        hideLoadingDialog(false);
    }

    public boolean showLoadingUi(final String text) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(CommonActivity.this);
        }
        return mLoadingDialog.showLoadingUi(this,text);
    }

    private void hideLoadingDialog(boolean isOnDestoryCalled){
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.hide();
        }
        if(mLoadingDialog != null && isOnDestoryCalled){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

}
