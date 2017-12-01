package com.tencent.appframework.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.appframework.R;


/**
 * Alert弹出框
 * @author fortunexiao
 */
public class AlertDialog extends BaseThemeDialog implements DialogInterface {

    // views
    private ImageView ivIcon;
    private TextView tvTitle;
    private TextView tvMessage;
    private Button btnPositive;
    private Button btnNetative;
    private Button btnNeutral;

    protected AlertDialog(Context context) {
        this(context, true, null);
    }

    protected AlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_alert);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        btnNetative = (Button) findViewById(R.id.btn_nagative);
        btnPositive = (Button) findViewById(R.id.btn_positive);
        btnNeutral = (Button) findViewById(R.id.btn_neutral);
    }

    public Button getButton(int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_NEGATIVE:
                return btnNetative;
            case DialogInterface.BUTTON_NEUTRAL:
                return btnNeutral;
            case DialogInterface.BUTTON_POSITIVE:
                return btnPositive;
            default:
                break;
        }
        return null;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }
    }

    public void setMessage(CharSequence message) {
        tvMessage.setText(message);
    }

    public void setButton(int whichButton, CharSequence text, final OnClickListener listener) {
        switch (whichButton) {
            case BUTTON_POSITIVE:
                btnPositive.setVisibility(View.VISIBLE);
                btnPositive.setText(text);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onClick(AlertDialog.this, BUTTON_POSITIVE);
                        }
                        dismiss();
                    }
                });
                break;
            case BUTTON_NEUTRAL:
                btnNeutral.setVisibility(View.VISIBLE);
                btnNeutral.setText(text);
                btnNeutral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onClick(AlertDialog.this, BUTTON_POSITIVE);
                        }
                        dismiss();
                    }
                });
                break;
            case BUTTON_NEGATIVE:
                btnNetative.setVisibility(View.VISIBLE);
                btnNetative.setText(text);
                btnNetative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onClick(AlertDialog.this, BUTTON_POSITIVE);
                        }
                        dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }

    public void setIcon(int resId) {
    }

    public void setIcon(Drawable icon) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * AlertDialog的构造器
     */
    public static class Builder {

        private Context mContext;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private int mIconId;
        private Drawable mIcon;

        private CharSequence mPositiveButtonText;
        private OnClickListener mPositiveButtonListener;

        private CharSequence mNegativeButtonText;
        private OnClickListener mNegativeButtonListener;

        private CharSequence mNeutralButtonText;
        private OnClickListener mNeutralButtonListener;

        private OnCancelListener mOnCancelListener;
        private OnDismissListener mOnDismissListener;
        private OnKeyListener mOnKeyListener;

        private boolean mCancelable = true;

        public Builder(Context context) {
            mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        public Builder setTitle(int titleId) {
            return setTitle(mContext.getString(titleId));
        }

        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public Builder setMessage(int messageId) {
            return setMessage(mContext.getText(messageId));
        }

        public Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public Builder setIcon(int iconId) {
            mIconId = iconId;
            return this;
        }

        public Builder setIcon(Drawable icon) {
            mIcon = icon;
            return this;
        }


        public Builder setPositiveButton(int textId, final OnClickListener listener) {
            return setPositiveButton(mContext.getText(textId), listener);
        }

        public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            mPositiveButtonText = text;
            mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId, final OnClickListener listener) {
            return setNegativeButton(mContext.getText(textId), listener);
        }

        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            mNegativeButtonText = text;
            mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(int textId, final OnClickListener listener) {
            return setNeutralButton(mContext.getText(textId), listener);
        }

        public Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
            mNeutralButtonText = text;
            mNeutralButtonListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            mOnKeyListener = onKeyListener;
            return this;
        }


        public AlertDialog create() {
            final AlertDialog dialog = new AlertDialog(mContext, false, mOnCancelListener);

            if (!TextUtils.isEmpty(mTitle)) {
                dialog.setTitle(mTitle);
            }

            dialog.setMessage(mMessage);

            if (mIconId != -1) {
                dialog.setIcon(mIconId);
            } else if (mIcon != null) {
                dialog.setIcon(mIcon);
            }

            if (!TextUtils.isEmpty(mPositiveButtonText)) {
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, mPositiveButtonText, mPositiveButtonListener);
            }

            if (!TextUtils.isEmpty(mNegativeButtonText)) {
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mNegativeButtonText, mNegativeButtonListener);
            }

            if (!TextUtils.isEmpty(mNeutralButtonText)) {
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, mNeutralButtonText, mNeutralButtonListener);
            }

            dialog.setCancelable(mCancelable);
            if (mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(mOnCancelListener);
            dialog.setOnDismissListener(mOnDismissListener);
            if (mOnKeyListener != null) {
                dialog.setOnKeyListener(mOnKeyListener);
            }

            return dialog;
        }

        public AlertDialog show() {
            AlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

}
