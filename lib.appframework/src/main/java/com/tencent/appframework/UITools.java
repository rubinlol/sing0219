
package com.tencent.appframework;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.appframework.init.ComponentContext;
import com.tencent.appframework.thread.ThreadPool;

import java.util.concurrent.atomic.AtomicInteger;

public class UITools {

    private static Handler mUIHandler = new Handler(Looper.getMainLooper());

    private UITools() {

    }

    public static void showToast(CharSequence text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public static void showToast(int resId) {
        showToast(resId, Toast.LENGTH_SHORT);
    }

    public static void showToast(int resId, int duration) {
        showToast(ComponentContext.getContext().getText(resId), duration);
    }

    public static void showToast(final CharSequence text, final int duration) {
        if (!TextUtils.isEmpty(text)) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                toastInner(text, duration);
            } else {
                mUIHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        toastInner(text, duration);
                    }
                });
            }
        }
    }

    /**
     * 非法字符的弹框
     */
    public static void showErrorCharToast() {
        showToast("含有非法字符");
    }

    private static void toastInner(CharSequence text, int duration) {
        if (!TextUtils.isEmpty(text)) {
            Toast toast = Toast.makeText(ComponentContext.getContext(), text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setText(text);
            toast.setDuration(duration);
            toast.show();
        }
    }

    public static void showDebugToast(CharSequence text) {
        showDebugToast(text, Toast.LENGTH_SHORT);
    }

    public static void showDebugToast(CharSequence text, int duration) {
//        if (DebugUtil.isDebuggable(ComponentContext.getContext())) {
//            String outStr = "debug版本调试信息：" + text;
//            showToast(outStr, duration);
//        }
    }

    public static void showDebugToast(int resId) {
        showDebugToast(resId, Toast.LENGTH_SHORT);
    }

    public static void showDebugToast(int resId, int duration) {
        showDebugToast(ComponentContext.getContext().getText(resId), duration);
    }

    public static final int getPixFromDip(float dips, final Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dips * scale + 0.5f);
    }

    public static final int getPixFromDip(float aDipValue) {

        return getPixFromDip(aDipValue, ComponentContext.getContext());
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {

        Rect frame = new Rect();

        if (activity != null) {
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        }

        return frame.top;
    }

    /***
     * 从系统剪贴板获取内容
     */
    public static String getClipBoardText() {
        String temp = "";
        ClipboardManager cm = (ClipboardManager) ComponentContext.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clipData = cm.getPrimaryClip();

        if (clipData != null) {

            ClipData.Item item = clipData.getItemAt(0);

            if (item != null && item.getText() != null) {
                temp = item.getText().toString().trim();
            }
        }
        cm.setPrimaryClip(ClipData.newPlainText("text", temp));
        return temp;
    }

    /**
     * 复制文本到粘贴板
     */
    public static void saveTextToClipboard(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("text", text));
    }


    /**
     * 获取当前屏幕方向
     *
     * @return 见Configuration.ORIENTATION_LANDSCAPE等等
     */
    public static int getScreenOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }


    /**
     * Generate a value suitable for use in {View.setId}. This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * 移动EditView光标
     */
    public static void moveEditSelector(final @NonNull EditText editText) {
        ThreadPool.runUITask(new Runnable() {
            @Override
            public void run() {
                Editable etext = editText.getText();
                int position = etext.length();
                Selection.setSelection(etext, position);
            }
        });
    }

}
