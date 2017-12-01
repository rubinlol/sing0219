package com.tencent.cmake;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tencent.appframework.activity.BaseActivity;
import com.tencent.appframework.adapter.component.ComponentMannager;
import com.tencent.appframework.adapter.component.ComponentName;
import com.tencent.draglayout.activity.DragLayoutActivity;
import com.tencent.draglayout.test.TestDragLayoutActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by rubinqiu on 2017/11/20.
 *
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "JniActivity";

    @BindView(R.id.ry)
    RecyclerView mRecyclerView;

    private ComponentMannager mComponentMannager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_app_main);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        mComponentMannager = ComponentMannager.newInstance(this);
        mComponentMannager.setCompareable(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mComponentMannager.getAdapter());
    }

    private void initData(){
        mComponentMannager.add(new ComponentName(DragLayoutActivity.class));
        mComponentMannager.add(new ComponentName(TestDragLayoutActivity.class));
    }

}