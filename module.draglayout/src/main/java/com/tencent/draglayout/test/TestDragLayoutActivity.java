package com.tencent.draglayout.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tencent.appframework.activity.BaseActivity;
import com.tencent.draglayout.R;

/**
 * Created by rubinqiu on 2017/11/23.
 * 测试DragViewGroup
 */
public class TestDragLayoutActivity extends BaseActivity {
    private static final String TAG = "TestDragLayoutActivity";

    public static void show(@NonNull  Context context){
        Intent intent = new Intent(context,TestDragLayoutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
    }


}
