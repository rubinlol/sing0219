package com.tencent.draglayout.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.tencent.appframework.UITools;
import com.tencent.appframework.activity.BaseActivity;
import com.tencent.appframework.dialog.PopMenuDialog;
import com.tencent.draglayout.R;

/**
 * Created by rubinqiu on 2017/11/23.
 * 测试DragViewGroup
 */
public class TestDragLayoutActivity extends BaseActivity {
    private static final String TAG = "TestDragLayoutActivity";

    private View autoView;

    public static void show(@NonNull  Context context){
        Intent intent = new Intent(context,TestDragLayoutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dragview_test);
        DragViewGroup dragll = (DragViewGroup)findViewById(R.id.dragviewgroup);
        autoView = findViewById(R.id.backview);
        dragll.setAutoBackViewClickListener(autoBackViewClickListener);
    }

    private View.OnClickListener autoBackViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopMenuDialog.Builder builder = PopMenuDialog.createBuilder(TestDragLayoutActivity.this);
            for(int i=0;i<3;i++){
                builder.addMenu("num" + (i + 1), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UITools.showToast("num");
                    }
                });
            }
            builder.create().show(autoView);
        }
    };
}
