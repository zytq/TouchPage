package com.ls.touchpage.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mac on 2017/12/14.
 */

public  class BaseActivity extends AppCompatActivity {

    protected Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除title
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);
        initView();


    }


    /**
     * 初始化界面的视图控件
     */
    protected void initView() {

    }

    /**
     * 获取布局layout的资源ID
     *
     * @return 资源ID
     */
    protected int getLayoutId() {
        return 0;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null)
            mUnbinder.unbind();
    }
}
