package com.tencent.appframework.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tencent.appframework.R;
import com.tencent.appframework.thread.ThreadPool;


/**
 *
 */
public class LoadingDialog extends BaseDialog {

    private ProgressBar progressBar;
    private TextView tvLoadingLabel;
    private ImageView loadingView;
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
        loadingView = (ImageView)findViewById(R.id.load_img);
        loadingView.setImageDrawable(animationDrawable);
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

    public boolean showLoadingUi(final Activity activity, final String text) {
        if (activity == null || activity.isFinishing()) return false;

        ThreadPool.runUITask(new Runnable() {
            @Override
            public void run() {
                setText(text);
                if (!isShowing() && !activity.isFinishing()) {
                    show();
                }
            }
        });
        return true;
    }
}
