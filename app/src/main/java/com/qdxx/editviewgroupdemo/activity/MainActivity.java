package com.qdxx.editviewgroupdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qdxx.editviewgroupdemo.R;
import com.qdxx.editviewgroupdemo.view.EditViewGroupSecond;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mRlMain;
    //private EditViewGroup mEditViewGroup;
    private EditViewGroupSecond mEditViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    //初始化视图
    private void initView() {
        mRlMain = this.findViewById(R.id.rl_main);
        //mEditViewGroup = new EditViewGroup(MainActivity.this);
        mEditViewGroup = new EditViewGroupSecond(MainActivity.this);
        //mEditViewGroup.setBackgroundColor(Color.WHITE);
        mRlMain.addView(mEditViewGroup);
        ViewGroup.LayoutParams layoutParams = mEditViewGroup.getLayoutParams();
        layoutParams.width = 500;
        layoutParams.height = 500;
        mEditViewGroup.setLayoutParams(layoutParams);

    }
}
