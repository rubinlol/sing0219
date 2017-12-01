package com.tencent.appframework.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

/**
 * 记住按钮选中态的弹出菜单
 * Created by ethamhuang on 2017/5/9.
 */
public class StatusOperateDialog extends OperateDialog{
    private final static String TAG = StatusOperateDialog.class.getSimpleName();

    private int selectedTextColor = 0;
    private Button lastSelectedButton = null;
    private int lastSelectedButtonTextColor;

    private int defaultSelectButtonPosition = 0;
    private int itemPosition = -1;

    public StatusOperateDialog(Context context) {
        super(context);
    }

    /**
     * 设置默认选中第几个按钮。如不设置，默认为0
     * @param position
     */
    public void setDefaultSelectButtonPosition(int position) {
        defaultSelectButtonPosition = position;
    }

    public void setSelectedTextColor(int color) {
        this.selectedTextColor = color;
    }

    private void updateSelectedButton(Button button) {
        lastSelectedButton = button;

        Menu m = (Menu) button.getTag();
        if(m != null && m.menuTitleColor != 0) {
            lastSelectedButtonTextColor = m.menuTitleColor;
        } else {
            lastSelectedButtonTextColor = lastSelectedButton.getTextColors().getDefaultColor();
        }
        lastSelectedButton.setTextColor(selectedTextColor);
    }

    @Override
    protected View createMenuItem(Menu menu) {
        View v = super.createMenuItem(menu);
        itemPosition ++;

        if(defaultSelectButtonPosition == itemPosition && lastSelectedButton == null && selectedTextColor != 0) {
            updateSelectedButton((Button) v);
        }

        return v;
    }

    @Override
    protected void onItemViewClick(View v) {

        Menu m = (Menu) v.getTag();
        if(m == null || m.onMenuClickListener == null || selectedTextColor == 0 || !(v instanceof Button)) {
            return;
        }

        if(lastSelectedButton == null) {
            updateSelectedButton((Button) v);
        } else if(lastSelectedButton != v) {
            lastSelectedButton.setTextColor(lastSelectedButtonTextColor);

            updateSelectedButton((Button) v);
        }
    }
}
