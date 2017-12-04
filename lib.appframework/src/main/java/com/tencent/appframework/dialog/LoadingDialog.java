package com.tencent.appframework.dialog;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tencent.appframework.R;


/**
 *
 */
public class LoadingDialog extends BaseDialog {

    private ProgressBar progressBar;
    private TextView tvLoadingLabel;
    private AnimationDrawable animationDrawable = (AnimationDrawable) getContext().getResources().getDrawable(R.drawable.loading_animation);

    public LoadingDialog(Context context) {
        super(context);
        init();
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        getContext().setTheme(R.style.DialogTheme);
        setContentView(R.layout.dialog_loading);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tvLoadingLabel = (TextView) findViewById(R.id.tv_loading_label);
        animationDrawable.setOneShot(false);
    }

    public void setText(String text) {
        tvLoadingLabel.setText(text);
    }

    @Override
    public void dismiss() {
        animationDrawable.stop();
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
        animationDrawable.start();
    }
}
