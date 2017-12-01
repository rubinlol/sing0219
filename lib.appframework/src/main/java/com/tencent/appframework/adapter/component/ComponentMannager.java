package com.tencent.appframework.adapter.component;

import android.content.Context;

import com.tencent.appframework.adapter.base.BaseViewTypeManager;


/**
 * Created by rubinqiu on 2017/3/9.
 * 把ComponentAdapter和ComponentName封装起来，然后用户只跟ComponentMannager耦合就可以了
 */
public class ComponentMannager extends BaseViewTypeManager<ComponentAdapter,ComponentName> {
    private static final String TAG = ComponentMannager.class.getSimpleName();

    public static ComponentMannager newInstance(Context context){
        return new ComponentMannager(context);
    }

    private ComponentMannager(Context context){
        super(context);
    }

    @Override
    public ComponentAdapter getAdapter() {
        return new ComponentAdapter(getContext(),getDatas());
    }
}
