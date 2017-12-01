package com.tencent.appframework.adapter.component;

/**
 * Created by rubinqiu on 2017/3/8.
 *
 */

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tencent.appframework.R;
import com.tencent.appframework.adapter.base.BaseViewTypeAdapter;

import java.util.List;


public class ComponentAdapter extends BaseViewTypeAdapter<ComponentName> {
    private static final int RECYCLER_VIEW_TYPE_COMPOENT_NAME = 0x01;

    public ComponentAdapter(Context context, List<ComponentName> datas) {
        super(context, datas);
        initViewTypes();
    }

    private void initViewTypes() {
        addViewType(RECYCLER_VIEW_TYPE_COMPOENT_NAME, CompoentViewHolder.class);
    }

    static class CompoentViewHolder extends BaseViewTypeAdapter.ViewTypeViewHolder<ComponentName> {
        private TextView mNameTxt;

        @Override
        protected void onCreate() {
            super.onCreate();
            setContentView(R.layout.recycler_item_layout);
            mNameTxt = (TextView)findViewById(R.id.show_name);
        }

        @Override
        protected void bindData(int position, ComponentName data) {
            if (data != null && !TextUtils.isEmpty(data.getName())) {
                mNameTxt.setText(data.getName());
                View contentView = getContentView();
                if(contentView != null){
                    contentView.setTag(data);
                }
            }
        }

        @Override
        protected View.OnClickListener getItemViewClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ComponentName componentName = (ComponentName) view.getTag();
                    getContext().startActivity(new Intent(getContext(), componentName.getActivityClass()));
                }
            };
        }
    }
}


