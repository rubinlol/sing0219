package com.tencent.appframework.dialog;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActionBar;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单Dialog
 * 从底部弹出的菜单Dialog
 * <p>
 * Created by fortunexiao on 2016/3/1.
 */
public class PopMenuDialog extends BaseDialog {

    static final String TAG = PopMenuDialog.class.getSimpleName();

    private static int MENU_HEIGHT;
    private static final int MAX_DURATION = 200;

    private LinearLayout llOprContainer;

    private float height;

    private Handler handler = new Handler(Looper.getMainLooper());

    public PopMenuDialog(Context context) {
        super(context);
        getContext().setTheme(android.R.style.Theme_Holo_Panel);
        setContentView(R.layout.dialog_pop_menu);
        setupWindow();
        llOprContainer = (LinearLayout) findViewById(R.id.ll_opr_container);
        MENU_HEIGHT = DensityUtil.dip2px(context, 45);
    }

    private void setupWindow() {
        Window window = getWindow();
        window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        int[] size = getScreenSize(getContext());
        int screenWidth = size[0];
        window.setLayout(screenWidth - DensityUtil.dip2px(getContext(), 32), ViewGroup.LayoutParams.WRAP_CONTENT);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(0.55f);
        window.setWindowAnimations(R.style.Animations_Empty_Duration_200);

        // 点击窗口外部的时候关闭Dialog
        setCanceledOnTouchOutside(true);
    }

    /**
     * 设置菜单操作项
     * @param menus 菜单列表
     */
    public void setMenus(List<Menu> menus) {
        if (menus == null || menus.size() == 0) {
            return;
        }

        llOprContainer.removeAllViews();
        height = 0;

        // 添加菜单View
        for (int i = 0; i < menus.size(); i++) {
            boolean widthLine = i != menus.size() - 1;
            addMenu(menus.get(i), i == 0, i == menus.size() - 1, menus.size());
            height += (MENU_HEIGHT + (widthLine ? 1 : 0));
        }
    }

    private View createMenuItem(final Menu menu) {
        View itemView;
        if (menu.customMenuView == null) {
            Button button = new Button(getContext());
            button.setText(menu.menuTitle);
            button.setTextColor(getContext().getResources().getColor(R.color.CT1));
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            // 判断是否用户设置了文字颜色
            if (menu.menuTitleColor != 0) {
                button.setTextColor(menu.menuTitleColor);
            }
            itemView = button;
        } else {
            ViewGroup viewGroup = new FrameLayout(getContext());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            viewGroup.addView(menu.customMenuView, lp);
            itemView = viewGroup;
        }
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

    private void addMenu(Menu menu, boolean isFirst, boolean isLast, int size) {
        View menuView = createMenuItem(menu);
        addMenuButton(menuView);
        if (size == 1) {
            menuView.setBackgroundResource(R.drawable.dialog_pop_menu_item_bg);
        } else {
            if (isFirst) {
                menuView.setBackgroundResource(R.drawable.dialog_pop_menu_item_top_bg);
            } else if (isLast) {
                menuView.setBackgroundResource(R.drawable.dialog_pop_menu_item_bottom_bg);
            } else {
                menuView.setBackgroundResource(R.drawable.dialog_pop_menu_item_center_bg);
            }
            if (!isLast) {
                View line = new View(getContext());
                line.setBackgroundColor(0xffefeff4);
                llOprContainer.addView(line, new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, 1));
            }
        }

    }

    private void addMenuButton(View menuButton) {
        llOprContainer.addView(menuButton, new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, MENU_HEIGHT));
    }

    private int position;

    private static final int POS_BOTTOM = 0;
    private static final int POS_UP = 1;

    public void show(View anchor) {
        int[] btnPos = getLocationInWindow(anchor);
        int y = btnPos[1] - getStatusBarHeight(getContext());
        int[] size = getScreenSize(getContext());
        int screenHeight = size[1];
        Window window = getWindow();
        boolean isTop;
        if (y + getMenuHeight() < screenHeight - DensityUtil.dip2px(getContext(), 48)) { // 弹在下面
            window.getAttributes().y = y + anchor.getHeight();
            position = POS_BOTTOM;
            isTop = false;
            llOprContainer.setBackgroundResource(R.drawable.pop_menu_bg_down);
        } else {
            window.getAttributes().y = y - getMenuHeight();
            position = POS_UP;
            isTop = true;
            llOprContainer.setBackgroundResource(R.drawable.pop_menu_bg_up);
        }
        startAnim(isTop, true);
        show();
    }

    @Override
    public void cancel() {
        startAnim(position == POS_UP, false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, MAX_DURATION);
    }

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    private static int[] getLocationInWindow(View v) {
        int[] loc = new int[2];
        v.getLocationInWindow(loc);
        return loc;
    }


    /**
     * 开启动画
     * @param isUp 上边还是在下面
     * @param isShow 展开还是关闭
     * @return
     */
    private void startAnim(boolean isUp, boolean isShow) {

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0, 0);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 0);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f, 1f);
        if (isShow) {
            scaleX.setFloatValues(0f, 1f);
            scaleY.setFloatValues(0f, 1f);
            alpha.setFloatValues(0, 1, 1);
        } else {
            scaleX.setFloatValues(1f, 0f);
            scaleY.setFloatValues(1f, 0f);
            alpha.setFloatValues(1, 1, 0);
        }
        int size[] = getScreenSize(getContext());
        int screenWidth = size[0];
        if (isUp) {
            llOprContainer.setPivotX(screenWidth - DensityUtil.dip2px(getContext(), 32 + 10));
            llOprContainer.setPivotY(getMenuHeight());
        } else  {
            llOprContainer.setPivotX(screenWidth - DensityUtil.dip2px(getContext(), 32 + 10));
            llOprContainer.setPivotY(0);
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(llOprContainer, scaleX, scaleY, alpha);
        objectAnimator.setDuration(MAX_DURATION);
        objectAnimator.start();
    }

    private int getMenuHeight() {
        return (int) height + DensityUtil.dip2px(getContext(), 10);
    }

    private static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return new int[] {width, height};
    }


    public static Builder createBuilder(Context context) {
        return new Builder(context);
    }


    /**
     * 构造器模式
     */
    public static class Builder {

        // 缓存目标对象的构建参数
        private Context mContext;
        private List<Menu> menus = new ArrayList<>();

        public Builder() {
        }

        public Builder(Context context) {
            mContext = context;
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

        /**
         * 在create方法里通过缓存
         * @return
         */
        public PopMenuDialog create() {
            if (mContext == null) {
                throw new RuntimeException("No Context Object");
            }

            if (menus.size() == 0) {
                throw new RuntimeException("At least add one menu");
            }

            final PopMenuDialog dialog = new PopMenuDialog(mContext);
            dialog.setMenus(menus);
            return dialog;
        }

        public PopMenuDialog show() {
            PopMenuDialog dialog = create();
            dialog.show();
            return dialog;
        }
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
            this.menuTitleColor = menuTitleColor;
            this.onMenuClickListener = onMenuClickListener;
        }

        public Menu() {
        }

        public Menu setMenuTitleColor(int menuTitleColor) {
            this.menuTitleColor = menuTitleColor;
            return this;
        }
    }

    private static class AnimationUtils {
        interface EasingsConstants {
            public static final Interpolator easeOutQuad = PathInterpolatorCompat.create(0.25f, 0.46f, 0.45f, 0.94f);
            public static final Interpolator easeOutExpo = PathInterpolatorCompat.create(0.19f, 1f, 0.22f, 1f);
        }
    }
}
