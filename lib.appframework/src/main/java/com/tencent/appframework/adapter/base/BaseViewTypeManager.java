package com.tencent.appframework.adapter.base;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rubinqiu on 2017/3/9.
 * 封装BaseViewTypeAdapter和ViewTypeData的抽象类，并且支持排序功能
 *
 */
public abstract class BaseViewTypeManager<T extends BaseViewTypeAdapter, D extends BaseViewTypeAdapter.ViewTypeData & Comparable<D>> {
    private static final String TAG = BaseViewTypeManager.class.getSimpleName();
    private T mAdapter;
    private List<D> mDatas;
    private Context mContext;
    private boolean mCanCompareable;

    protected BaseViewTypeManager(Context context){
        mContext = context;
        mDatas = new ArrayList<>();
        mAdapter = getAdapter();
        if(mAdapter == null)throw new RuntimeException("adapter is null,please set it !");
    }

    public void setCompareable(boolean canCompareable){
        mCanCompareable = canCompareable;
    }

    public int getViewTypeCount(){
        return mAdapter.getViweTypeCount();
    }

    public final void add(@NonNull D data) {
        if(mDatas != null){
            mDatas.add(data);
            innerSortList(mDatas);
            mAdapter.notifyItemInserted(mDatas.size() -1);
        }
    }

    public final void addAll(@NonNull List<D> datas) {
        if(mDatas != null){
            mDatas.addAll(datas);
            innerSortList(mDatas);
            mAdapter.notifyItemRangeInserted(mDatas.size() - datas.size() -1,datas.size());
        }
    }

    public void refresh(@NonNull List<D> datas) {
        mDatas.clear();
        innerSortList(mDatas);
        mAdapter.notifyItemRangeInserted(0,mDatas.size());
    }

    public final List<D> getDatas(){
        return mDatas;
    }

    private void innerSortList(List<D> datas){
        if(mCanCompareable){
            Collections.sort(datas);
        }
    }

    public final void clear(){
        if(mDatas == null)return;
        mAdapter.notifyItemRangeRemoved(0,mDatas.size());
        mDatas.clear();
    }

    public final boolean remove(@NonNull D d) {
        if(mDatas == null)return false;
        boolean result = false;
        if(!mDatas.isEmpty() && mDatas.contains(d)){
            int position = mDatas.indexOf(d);
            mAdapter.notifyItemRemoved(position);
            mDatas.remove(position);
            result = true;
        }
        return result;
    }

    public final boolean remove(int position) {
        if(mDatas == null || (position >= mDatas.size() && position < 0))return false;
        boolean result = false;
        if(!mDatas.isEmpty()){
            mAdapter.notifyItemRemoved(position);
            mDatas.remove(position);
            result = true;
        }
        return result;
    }

    public void swap(int fromPosition, int toPosition) {
        if (mDatas == null) {
            return;
        }
        Collections.swap(mDatas, fromPosition, toPosition);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    public final Context getContext(){
        return mContext;
    }

    /**
     * 通过子类创建具体的Adapter
     **/
    public abstract T getAdapter();
}
