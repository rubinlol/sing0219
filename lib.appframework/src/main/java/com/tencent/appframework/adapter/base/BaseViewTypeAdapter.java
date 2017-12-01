package com.tencent.appframework.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.List;

import butterknife.ButterKnife;


/**
 * 支持ViewTypeData的Adapter for RecycleView
 *
 * 该类允许子类设置特定的ViewType，并为每个ViewType配置一个特定的ViewTypeViewHolder类，并把不同ViewType
 * 绑定数据的代码分发到特定的ViewTypeHolder,以防止Adapter代码的臃肿
 * Created by fortunexiao on 2016/12/15.
 */
public class BaseViewTypeAdapter<T extends BaseViewTypeAdapter.ViewTypeData> extends RecyclerView.Adapter {

    static final String TAG = BaseViewTypeAdapter.class.getSimpleName();

    private Context mContext;
    private List<T> mDatas;

    private SparseArray<Class<? extends ViewTypeViewHolder>> mItems = new SparseArray<>();

    /**
     * 构造器
     *
     * @param context 上下文
     * @param datas   数据列表
     */
    public BaseViewTypeAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    public Context getContext() {
        return mContext;
    }

    public int getViweTypeCount(){
        return mItems == null?0:mItems.size();
    }

    /**
     * 增加一个ViewType
     *
     * @param viewType        viewType
     * @param viewHolderClass ViewType对应的ViewHolder类
     */
    public void addViewType(int viewType, Class<? extends ViewTypeViewHolder> viewHolderClass) {
        mItems.put(viewType, viewHolderClass);
    }

    @Override
    public int getItemViewType(int position) {
        T t = getItem(position);
        if (t != null) {
            return t.getViewType();
        }
        return 0;
    }

    @Override
    public final StubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Class<? extends ViewTypeViewHolder> viewHolderClazz = mItems.get(viewType);

        if (viewHolderClazz == null) {
            viewHolderClazz = EmptyViewHolder.class;
        }

        ViewTypeViewHolder holder;
        try {
            holder = viewHolderClazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        holder.setContext(mContext);
        holder.setAdapter(this);

        holder.onCreate();
        holder.onPostCreate();

        onViewHolderCreated(holder);

        return holder.viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StubViewHolder) {
            StubViewHolder svh = (StubViewHolder) holder;
            if (svh.viewTypeViewHolder != null) {
                svh.viewTypeViewHolder.bindData(position, getItem(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    /**
     * ViewHolder被创建的回调
     * 允许用户在ViewHolder创建的时候个性初始化ViewHolder
     *
     * @param holder 创建的ViewHolder
     */
    protected void onViewHolderCreated(ViewTypeViewHolder holder) {
    }

    private T getItem(int position) {
        return mDatas != null ? mDatas.get(position) : null;
    }

    /**
     * 当用户配置ViewType的时候同时需要配置一个ViewTypeViewHolder类
     * 该类负责为特定的ViewType绑定数据
     *
     * @param <T>
     */
    public static abstract class ViewTypeViewHolder<T> {

        private StubViewHolder viewHolder;
        private RecyclerView.Adapter mAdapter;

        private Context context;

        // === methods ===

        private void setContext(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        protected void onCreate() {
        }

        protected void onPostCreate() {
        }

        public void setContentView(int resId) {
            View itemView = LayoutInflater.from(getContext()).inflate(resId, null);
            setContentView(itemView);
        }

        public void setContentView(View view) {
            viewHolder = new StubViewHolder(view);
            viewHolder.viewTypeViewHolder = this;
            ButterKnife.bind(this, viewHolder.itemView);
            viewHolder.itemView.setOnClickListener(getItemViewClickListener());
        }

        public View getContentView(){
            return viewHolder.itemView;
        }

        private void setAdapter(RecyclerView.Adapter adapter) {
            this.mAdapter = adapter;
        }

        public RecyclerView.Adapter getAdapter() {
            return mAdapter;
        }

        public View findViewById(int id) {
            if (viewHolder != null) {
                return viewHolder.itemView.findViewById(id);
            }
            return null;
        }

        /**
         * 绑定item的数据
         *
         * @param position 位置
         * @param data     数据
         */
        protected abstract void bindData(int position, T data);

        /**
         * 通过子View获取OnClickListener
         * @return viewHolder.itemView点击监听器
         */
        protected abstract View.OnClickListener getItemViewClickListener();
    }

    public static class StubViewHolder<T> extends RecyclerView.ViewHolder {
        ViewTypeViewHolder<T> viewTypeViewHolder;

        public StubViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 要支持ViewTypeAdapter数据源必须实现该接口
     */
    public interface ViewTypeData {

        /**
         * 返回数据源对应的viewType
         *
         * @return viewType
         */
        int getViewType();
    }


    /**
     * 当数据源返回一个没有配置的ViewType的时候，该Item隐藏
     */
    public static class EmptyViewHolder extends ViewTypeViewHolder {

        @Override
        protected void onCreate() {
            super.onCreate();
            View emptyView = new View(getContext());
            emptyView.setLayoutParams(new AbsListView.LayoutParams(0, 0));
            setContentView(emptyView);
        }

        @Override
        public void bindData(int position, Object data) {
            // do nothing

        }

        @Override
        protected View.OnClickListener getItemViewClickListener() {
            return null;
        }
    }
}
