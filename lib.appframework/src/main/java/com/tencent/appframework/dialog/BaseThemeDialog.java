package com.tencent.appframework.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.appframework.R;


/**
 * 基础Dialog提供公共的动画和边框样式
 *
 * Created by fortunexiao on 2016/1/13.
 */
public class BaseThemeDialog extends BaseDialog {

    static final String TAG = BaseThemeDialog.class.getSimpleName();

    private ViewGroup contentContainer;

    protected Window mWindow;

    public BaseThemeDialog(Context context) {
        super(context);
        init();
    }

    public BaseThemeDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    protected BaseThemeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public View getContentView() {
        return contentContainer;
    }

    private void init() {
        getContext().setTheme(R.style.DialogTheme);

//        super.setContentView(R.layout.dialog_base_theme);
//        contentContainer = (ViewGroup) findViewById(R.id.content);
        contentContainer = (ViewGroup)findViewById(Window.ID_ANDROID_CONTENT);

        mWindow = getWindow();
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mWindow.setDimAmount(0.6f);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.8);
        mWindow.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void setContentView(int layoutResID) {
        contentContainer.removeAllViews();
        LayoutInflater.from(getContext()).inflate(layoutResID, contentContainer, true);
    }

    @Override
    public void setContentView(View view) {
        contentContainer.removeAllViews();
        contentContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        contentContainer.removeAllViews();
        contentContainer.addView(view, params);
    }
}
