package com.tencent.appframework.dialog;

import android.app.Dialog;
import android.content.Context;

/**
 * 为了和系统的Dialog做一个隔断
 * Created by fortunexiao on 2016/1/13.
 */
public class BaseDialog extends Dialog {

    static final String TAG = BaseDialog.class.getSimpleName();

    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
