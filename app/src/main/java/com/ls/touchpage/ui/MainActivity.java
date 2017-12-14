package com.ls.touchpage.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.ls.touchpage.R;
import com.ls.touchpage.base.BaseActivity;
import com.ls.touchpage.ui.activity.TouchPageActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.btn_touch_page)
    Button mBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @OnClick({R.id.btn_touch_page})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_touch_page://上下切换页面
                startActivity(new Intent(MainActivity.this, TouchPageActivity.class));
                break;
        }

    }
}
