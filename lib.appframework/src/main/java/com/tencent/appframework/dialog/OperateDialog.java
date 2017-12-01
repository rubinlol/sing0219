package com.tencent.appframework.dialog;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单Dialog
 * 从底部弹出的菜单Dialog
 * <p>
 * Created by fortunexiao on 2016/3/1.
 */
public class OperateDialog extends BaseDialog {

    static final String TAG = OperateDialog.class.getSimpleName();

    private static final int MENU_HEIGHT = 56;
    private static final int MAX_DURATION = 550;

    private LinearLayout llOprContainer;

    private long duration = 0;
    private float height;

    public OperateDialog(Context context) {
        super(context);
        getContext().setTheme(android.R.style.Theme_Holo_Panel);
        setContentView(R.layout.dialog_operate);
        setupWindow();
        llOprContainer = (LinearLayout) findViewById(R.id.ll_opr_container);
    }

    private void setupWindow() {
        Window window = getWindow();

        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(0.55f);
        window.setWindowAnimations(R.style.Animations_Empty_Duration_200);

        // 点击窗口外部的时候关闭Dialog
        setCanceledOnTouchOutside(true);
    }

    /**
     * 设置菜单操作项
     *
     * @param menus             菜单列表
     * @param widthCancelButton 是否带取消按钮
     */
    public void setMenus(List<Menu> menus, boolean widthCancelButton) {
        if (menus == null || menus.size() == 0) {
            return;
        }

        llOprContainer.removeAllViews();
        height = 0;

        // 添加菜单View
        for (int i = 0; i < menus.size(); i++) {
            addMenu(menus.get(i));
            height += DensityUtil.dip2px(getContext(), MENU_HEIGHT);
        }

        if (widthCancelButton) {
            // 分割线
            View v = new View(getContext());
            v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 4)));
            llOprContainer.addView(v);
            height += DensityUtil.dip2px(getContext(), 4);

            // 取消按钮
            View cancelButton = createMenuItem(new Menu("取消", null));
            addMenuButton(cancelButton);
            height += DensityUtil.dip2px(getContext(), MENU_HEIGHT);
        }

        duration = (menus.size() + (widthCancelButton ? 1 : 0)) * 116;
        if (duration > MAX_DURATION) {
            duration = MAX_DURATION;
        }

        llOprContainer.setY(height);
    }

    protected View createMenuItem(final Menu menu) {
        View itemView;

        if (menu.customMenuView == null) {
            Button button = new Button(getContext());
            button.setText(menu.menuTitle);
            button.setTextColor(getContext().getResources().getColor(R.color.CT1));

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
        itemView.setBackgroundResource(R.drawable.selector_dialog_operate_menu_bg);

        itemView.setTag(menu);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu.onMenuClickListener != null) {
                    menu.onMenuClickListener.onClick(v);
                }
                onItemViewClick(v);
                cancel();
            }
        });
        return itemView;
    }

    protected void onItemViewClick(View v) {

    }

    private void addMenu(Menu menu) {
        View menuView = createMenuItem(menu);
        addMenuButton(menuView);
    }

    private void addMenuButton(View menuButton) {
        int height = DensityUtil.dip2px(getContext(), MENU_HEIGHT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, height);
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
            this(menuTitle, onMenuClickListener, 0);
        }

        public Menu(String menuTitle, View.OnClickListener onMenuClickListener, int menuTitleColor) {
            this.menuTitle = menuTitle;
            this.onMenuClickListener = onMenuClickListener;
            this.menuTitleColor = menuTitleColor;
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
        long delay = Math.max(duration - 200, 200);
        Log.d(TAG, "delay = " + delay);
        ThreadPool.runUITask(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, delay);
    }


    private ObjectAnimator genAnimation(float y) {
        final ObjectAnimator animator = ObjectAnimator.ofFloat(llOprContainer, "y", y).setDuration(duration);
        animator.setInterpolator(AnimationUtils.EasingsConstants.easeOutExpo);
        return animator;
    }

    public static Builder createBuidler(Context context) {
        return new Builder(context);
    }


    /**
     * 构造器模式
     */
    public static class Builder {
        private Context mContext;
        private List<Menu> menus = new ArrayList<>();
        private boolean widthCancelButton;

        public Builder() {

        }

        public Builder(Context context) {
            mContext = context;
        }


        public Builder setMenus(List<Menu> menus, boolean widthCancelButton) {
            this.menus.addAll(menus);
            this.widthCancelButton = widthCancelButton;
            return this;
        }

        public Builder setMenus(List<Menu> menus) {
            this.menus.addAll(menus);
            return this;
        }

        public Builder addMenu(Menu menu) {
            this.menus.add(menu);
            return this;
        }

        public Builder addMenu(String menuTitle, View.OnClickListener onMenuClickListener) {
            return addMenu(menuTitle, onMenuClickListener, 0);
        }

        public Builder addMenu(String menuTitle, View.OnClickListener onMenuClickListener, int menuTextColor) {
            return addMenu(new Menu(menuTitle, onMenuClickListener, menuTextColor));
        }

        public Builder setWithCancelButton(boolean widthCancelButton) {
            this.widthCancelButton = widthCancelButton;
            return this;
        }

        public OperateDialog create() {
            if (mContext == null) {
                throw new RuntimeException("No Context Object");
            }

            if (menus.size() == 0) {
                throw new RuntimeException("At least add one menu");
            }

            final OperateDialog dialog = new OperateDialog(mContext);
            dialog.setMenus(menus, widthCancelButton);
            return dialog;
        }

        public OperateDialog show() {
            OperateDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

    private static class AnimationUtils {
        interface EasingsConstants {
            public static final Interpolator easeOutQuad = PathInterpolatorCompat.create(0.25f, 0.46f, 0.45f, 0.94f);
            public static final Interpolator easeOutExpo = PathInterpolatorCompat.create(0.19f, 1f, 0.22f, 1f);
        }
    }
}
