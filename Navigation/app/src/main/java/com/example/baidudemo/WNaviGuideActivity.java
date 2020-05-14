package com.example.baidudemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;

public class WNaviGuideActivity extends Activity {
    private WalkNavigateHelper mNaviHelper = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取WalkNavigateHelper实例
        mNaviHelper = WalkNavigateHelper.getInstance();
        //获取诱导页面地图展示View
        View view = mNaviHelper.onCreate(WNaviGuideActivity.this);
        if (view != null) {
            setContentView(view);
        }
        mNaviHelper.startWalkNavi(WNaviGuideActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNaviHelper.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
    }
}
