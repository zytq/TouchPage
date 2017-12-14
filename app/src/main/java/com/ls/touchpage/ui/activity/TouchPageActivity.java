package com.ls.touchpage.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.ls.touchpage.R;
import com.ls.touchpage.base.BaseActivity;
import com.ls.touchpage.util.DeviceUtils;

import butterknife.BindView;

public class TouchPageActivity extends BaseActivity {

    private static final int STATE_NORMAL = 0x000; // 正常状态（没有显示头部布局和底部布局）
    private static final int STATE_PULLING = 0x001; // 正在下拉或上拉，但没有达到加载上一个房间或加载下一个房间的要求的状态
    private static final int STATE_PREPARED = 0x002; // 达到刷新或加载的要求，松开手指就可以加载上一个房间或加载下一个房间的状态
    private static final int STATE_REFRESHING = 0x003; // 正在加载上一个房间或加载下一个房间的状态

    @BindView(R.id.live_loading)
    View mLoadding;

    @BindView(R.id.load_head)
    View mTopLayout;//上面的布局


    @BindView(R.id.load_foot)
    View mFootLayout;//底部的布局


    private boolean isRefreshable; // 是否可以下拉刷新下一个房间的数据

    private boolean isLoadable; // 是否可以上拉加载上一个房间的数据
    private float moveHeight= 250;//有效移动距离
    private float startY; // 记录手指按下时的Y坐标位置
    private float offsetY; // 记录手指拖动过程中Y坐标的偏移量
    private float screenHeight = 0;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_touch_page;
    }

    @Override
    protected void initView() {
        super.initView();

        screenHeight = DeviceUtils.getScreenHeight(this)- DeviceUtils.getStatusBarHeight(this);
        setCurrentState(STATE_NORMAL);

    }

    /**
     * 监听手指操作的事件（按下、滑动、抬起）
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            // 手指按下时，判断是否可以下拉或上拉加载
            case MotionEvent.ACTION_DOWN:
                isRefreshable = false;
                isLoadable = false;
                startY = (int) ev.getY();
                break;
            // 手指移动时，判断是否在下拉或上拉加载，如果是，则动态改变头部布局或底部布局的状态
            case MotionEvent.ACTION_MOVE:
                offsetY =  ev.getY() - startY;//下拉大于0
                if (offsetY > 0) {//向下拉  加载上一个房间的数据
                    setHeadMagin((int) (screenHeight-offsetY));
                    setFootMargin((int) (screenHeight));
                    if (offsetY >= moveHeight) {
                        isRefreshable = true;
                    } else {
                        isRefreshable = false;
                    }
                } else if (offsetY < 0) {//向上拖  加载下一个房间的数据
                    setFootMargin( (int) (screenHeight+offsetY));
                    setHeadMagin( (int) (screenHeight));
                    if (offsetY <= -moveHeight) {
                        isLoadable = true;
                    } else {
                        isLoadable = false;
                    }
                }
                break;
            // 手指抬起时，判断是否下拉或上拉到可以加载房间的程度，如果达到程度，则进行加载
            case MotionEvent.ACTION_UP:
                if (offsetY > 0) {//向下拉  加载上一个房间的数据
                    if (isRefreshable) {
                        setCurrentState(STATE_PREPARED);
                    } else {
                        setCurrentState(STATE_PULLING);
                    }
                } else if (offsetY < 0) {
                    if (isLoadable) {
                        setCurrentState(STATE_PREPARED);
                    } else {
                        setCurrentState(STATE_PULLING);
                    }
                }
                isRefreshable = false;
                isLoadable = false;
                break;
        }
//        return super.onTouchEvent(ev);
        return true;
    }

    /**
     * 根据当前的状态进行相应的处理
     */
    private void setCurrentState(int state) {
        switch (state) {
            // 普通状态：头部布局和尾部布局都隐藏，头部布局中不显示进度条，底部布局中不显示进度条
            case STATE_NORMAL:
                setHeadMagin((int) screenHeight);
                setFootMargin((int) screenHeight);
                mTopLayout.setVisibility(View.VISIBLE);
                mFootLayout.setVisibility(View.VISIBLE);
                break;
            // 正在下拉后上拉，但没有达到加载房间的要求的状态：
            case STATE_PULLING:
                if (offsetY>0) //下拉回退
                    setHeadMagin((int) screenHeight);
                    else      //上拉回退
                    setFootMargin((int) screenHeight);
                break;
            // 正在准备加载房间的状态：
            case STATE_PREPARED:
                if (offsetY>0&&isRefreshable) {//下拉加载下一个房间的数据
                    setHeadMagin(0);
                    setCurrentState(STATE_REFRESHING);
                } else if (offsetY<0 && isLoadable){//上拉加载上一个房间的数据
                    setFootMargin(0);
                    setCurrentState(STATE_REFRESHING);
                }
                break;
            case STATE_REFRESHING:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (offsetY>0) {
                            setHeadMagin((int) screenHeight);
                        } else {
                            setFootMargin((int)screenHeight);
                        }
                        mLoadding.setVisibility(View.VISIBLE);
                    }
                },400);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoadding.setVisibility(View.GONE);
                    }
                },3000);

                break;
        }
    }

    /**
     * 设置头部布局位置
     */
    private void setHeadMagin(int marginBottom) {
        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) mTopLayout.getLayoutParams();
        lp1.bottomMargin = marginBottom;
        mTopLayout.setLayoutParams(lp1);
    }
    /**
     * 设置底部布局位置
     */
    private void setFootMargin(int maginTop)  {
        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) mFootLayout.getLayoutParams();
        lp2.topMargin = maginTop;
        mFootLayout.setLayoutParams(lp2);
    }

}
