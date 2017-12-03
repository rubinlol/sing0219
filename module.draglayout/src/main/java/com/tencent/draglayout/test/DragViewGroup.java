package com.tencent.draglayout.test;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.tencent.appframework.swipeback.ViewDragHelper;

/**
 * Created by rubinqiu on 2017/11/28.
 * 参考:https://www.kancloud.cn/digest/fastdev4android/109672 类似于QQ侧滑组件
 */
public class DragViewGroup extends LinearLayout {
    private static final String TAG = "DragViewGroup";
    private ViewDragHelper dragHelper;
    private Context mContext;

    private View mDragView;
    private View mAutoBackView;
    private View mEdgeTrackerView;

    private Point mAutoBackOriginPos = new Point();
    private View.OnClickListener mAutoBackClickListener;

    public DragViewGroup(Context context) {
        super(context);
        init(context);
    }

    public DragViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        dragHelper = ViewDragHelper.create(this, callback);
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT);//添加边界检测
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mAutoBackOriginPos.x = mAutoBackView.getLeft();
        mAutoBackOriginPos.y = mAutoBackView.getTop();
    }

    public View findTopChildUnder(int x, int y) {
        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (x >= child.getLeft() && x < child.getRight() && y >= child.getTop() && y < child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    //接管touch事件
    @Override
    public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
        if(mAutoBackClickListener != null){
            final float x = ev.getX();
            final float y = ev.getY();
            final View topChild = findTopChildUnder((int) x, (int) y);
            if(topChild == mAutoBackView){
                return false;
            }
        }
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    //获取拖拽的子View
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDragView = getChildAt(0);
        mAutoBackView = getChildAt(1);
        mEdgeTrackerView = getChildAt(2);
    }

    public void  setAutoBackViewClickListener(View.OnClickListener listener){
        mAutoBackClickListener = listener;
        mAutoBackView.setOnClickListener(mAutoBackClickListener);
    }

    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    //自定义callback
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //mEdgeTrackerView禁止直接移动
            return child == mDragView || child == mAutoBackView;
        }

        //可以在该方法中对child移动的边界进行控制，left , top 分别为即将移动到的位置，比如横向的情况下，我希望只在ViewGroup的内部移动
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - mDragView.getWidth() - leftBound;
            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
            return newLeft;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mDragView.getHeight() - topBound;
            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            dragHelper.captureChildView(mEdgeTrackerView, pointerId);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //mAutoBackView手指释放时可以自动回去
            if (releasedChild == mAutoBackView) {
                dragHelper.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
                invalidate();
            }
        }
    };


}
