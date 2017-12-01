package com.tencent.appframework.dialog;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tencent.appframework.DensityUtil;
import com.tencent.appframework.R;
import com.tencent.appframework.thread.ThreadPool;

import java.util.List;

/**
 * 菜单Dialog
 * 从底部弹出的菜单Dialog
 *
 * Created by rubinqiu on 2016/3/1.
 */
public class MenuDialog extends Dialog {

    static final String TAG = MenuDialog.class.getSimpleName();

    static final int MENU_HEIGHT = 56;

    private LinearLayout llOprContainer;
    private long duration = 0;
    private float height;
    private Window mWindow;

    public MenuDialog(Context context) {
        super(context);
        getContext().setTheme(android.R.style.Theme_Holo_Panel);
        setContentView(R.layout.dialog_operate);
        setupWindow();
        llOprContainer = (LinearLayout) findViewById(R.id.ll_opr_container);
    }

    private void setupWindow() {
        mWindow = getWindow();

        mWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.95);
        mWindow.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mWindow.setDimAmount(0.55f);
        mWindow.setWindowAnimations(R.style.Animations_Empty_Duration_200);

        WindowManager.LayoutParams wmlp = mWindow.getAttributes();
        wmlp.y= DensityUtil.dip2px(getContext(),10);
        mWindow.setAttributes(wmlp);


        // 点击窗口外部的时候关闭Dialog
        setCanceledOnTouchOutside(true);
    }

    /**
     * 设置对话框的宽度，按比例计算
     * @param scaleWidth 值范围0~1，比如0.8就是整个屏幕的80%
     */
    public void setDialogWidth(float scaleWidth){
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * scaleWidth);
        mWindow.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 设置菜单操作项
     * @param menus 菜单列表
     * @param widthCancelButton 是否带取消按钮
     */
    public void setMenus(List<Menu> menus, boolean widthCancelButton) {
        if (menus == null || menus.size() == 0) {
            return;
        }

        llOprContainer.removeAllViews();
        height = 0;

        // 添加菜单View
        for (int i=0; i<menus.size(); i++) {
            addMenu(menus.get(i));
            height +=  DensityUtil.dip2px(getContext(), MENU_HEIGHT);
        }

        if (widthCancelButton) {
            // 分割线
            View v = new View(getContext());
            v.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.C5));
            v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 10)));
            llOprContainer.addView(v);
            height += DensityUtil.dip2px(getContext(), 5);

            // 取消按钮
            Menu cancelMenu = new Menu("取消", null);
            cancelMenu.setMenuTitleColor(ContextCompat.getColor(getContext(),R.color.C7));
            View cancelButton = createMenuItem(cancelMenu);
            addMenuButton(cancelButton);
            cancelButton.setBackgroundResource(R.drawable.selector_dialog_operate_top_menu_bg);
            height +=  DensityUtil.dip2px(getContext(), MENU_HEIGHT);
        }

        duration = (menus.size() + (widthCancelButton ? 1 : 0)) * 116;

        llOprContainer.setY(height);
    }

    private View createMenuItem(final Menu menu) {
        View itemView = null;

        if (menu.customMenuView == null) {
            Button button = new Button(getContext());
            button.setText(menu.menuTitle);
            button.setTextColor(ContextCompat.getColor(getContext(),R.color.CT1));

            // 判断是否用户设置了文字颜色
            if (menu.menuTitleColor != 0) {
                button.setTextColor(menu.menuTitleColor);
            }

            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.T2));
            itemView = button;
        } else {
            ViewGroup viewGroup = new FrameLayout(getContext());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            viewGroup.addView(menu.customMenuView, lp);
            itemView = viewGroup;
        }

        if(llOprContainer.getChildCount() == 0){
            itemView.setBackgroundResource(R.drawable.selector_dialog_operate_top_menu_bg);
        }else {
            itemView.setBackgroundResource(R.drawable.selector_dialog_operate_menu_bg);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu.onMenuClickListener != null) {
                    menu.onMenuClickListener.onClick(v);
                }
                cancel();
            }
        });

        return itemView;
    }

    private void addMenu(Menu menu) {
        View menuView = createMenuItem(menu);
        addMenuButton(menuView);
    }

    /**
     * 提交menuBtn
     * @param menuButton
     */
    private void addMenuButton(View menuButton) {
        int height = DensityUtil.dip2px(getContext(), MENU_HEIGHT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, height);
        if(llOprContainer.getChildCount() == 0){
            lp.setMargins(0,DensityUtil.dip2px(getContext(),4),0,0);//第一个View增加top间距这样就不会覆盖llOprContainer的圆角
        }
        llOprContainer.addView(menuButton, lp);
    }

    /**
     * 菜单对象
     */
    public static class Menu {
        public String menuTitle = null; // 标题文字
        public int menuTitleColor = 0;  // 标题颜色
        public View.OnClickListener onMenuClickListener; // 标题点击监听器
        public View customMenuView;

        public Menu(String menuTitle, View.OnClickListener onMenuClickListener) {
            this.menuTitle = menuTitle;
            this.onMenuClickListener = onMenuClickListener;
        }

        public Menu() {
        }

        public Menu setMenuTitleColor(int menuTitleColor) {
            this.menuTitleColor = menuTitleColor;
            return this;
        }
    }


    @Override
    public void show() {
        super.show();
        genAnimation(0).start();
    }

    @Override
    public void cancel() {

        ObjectAnimator animator = genAnimation(height);

        animator.start();
        long delay = Math.max(duration-200, 100);
        ThreadPool.runUITask(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, delay);
    }

    ObjectAnimator genAnimation(float y) {
        final ObjectAnimator animator = ObjectAnimator.ofFloat(llOprContainer, "y", y).setDuration(duration);
        animator.setInterpolator(EasingsConstants.easeOutExpo);
        return animator;
    }

    public interface EasingsConstants {
        Interpolator easeOutQuad = PathInterpolatorCompat.create(0.25f, 0.46f, 0.45f, 0.94f);
        Interpolator easeOutExpo = PathInterpolatorCompat.create(0.19f, 1f, 0.22f, 1f);
    }
}
