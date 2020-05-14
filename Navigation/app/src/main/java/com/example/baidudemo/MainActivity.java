package com.example.baidudemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWNaviCalcRouteListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.RouteNodeType;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final int PERMISSIONS_REQUEST = 0;
    private static final String TAG = "MainActivity";
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Button button = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取权限
        List<String> permissionList = new ArrayList<>();
        // 无需要申请的权限
        if(!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST);
        }

        setContentView(R.layout.main_activity);
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routePlanWithRouteNode();
            }
        });

        mBaiduMap.setIndoorEnable(true);//打开室内图，默认为关闭状态
        mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
        @Override
        public void onBaseIndoorMapMode(boolean on, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
            if (on && mapBaseIndoorMapInfo != null) {
                // 进入室内图
                // 通过获取回调参数 mapBaseIndoorMapInfo 便可获取室内图信
                //息，包含楼层信息，室内ID等
                // 切换楼层信息
                //strID 通过 mMapBaseIndoorMapInfo.getID()方法获得
                MapBaseIndoorMapInfo.SwitchFloorError switchFloorError =
                        mBaiduMap.switchBaseIndoorMapFloor(mapBaseIndoorMapInfo.getCurFloor(), mapBaseIndoorMapInfo.getID());
                Log.d(TAG, "switch floor status: " + switchFloorError.toString());
            } else {
                // 移除室内图
            }
        }
    });

        // 获取导航控制类
        // 引擎初始化
        WalkNavigateHelper.getInstance().initNaviEngine(this, new IWEngineInitListener() {

            @Override
            public void engineInitSuccess() {
                //引擎初始化成功的回调
                Log.d(TAG, "engine initialize success");
                routePlanWithRouteNode ();
            }

            @Override
            public void engineInitFail() {
                //引擎初始化失败的回调
                Log.d(TAG, "engine initialize fail");
            }
        });
    }

    private void routePlanWithRouteNode() {
        //起终点位置
        LatLng startPt = new LatLng(40.056015,116.3078);// 百度大厦
        WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
        walkStartNode.setKeyword("百度大厦");
        walkStartNode.setLocation(startPt);
        walkStartNode.setType(RouteNodeType.KEYWORD);
        walkStartNode.setCitycode(131);

        LatLng endPt = new LatLng(40.035919,116.339863);
        WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
        walkEndNode.setLocation(endPt);
        walkEndNode.setType(RouteNodeType.KEYWORD);
        walkEndNode.setKeyword("麻辣诱惑(五彩城店) ");
        walkEndNode.setBuildingID("1260176407175102463");
        walkEndNode.setFloorID("F4");
        walkEndNode.setCitycode(131);
        WalkNaviLaunchParam walkParam = new WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode);
        //发起路线规划
        WalkNavigateHelper.getInstance().routePlanWithRouteNode(walkParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                //开始算路的回调
                Log.d(TAG, "route plan start");
            }

            @Override
            public void onRoutePlanSuccess() {
                //算路成功
                Log.d(TAG, "route plan success");
                naviCalcRoute(0);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                //算路失败的回调
                Log.d(TAG, "route plan fail because " + walkRoutePlanError.toString());
            }
        });
    }

    private void naviCalcRoute(int routeIndex) {
        WalkNavigateHelper.getInstance().naviCalcRoute(routeIndex, new IWNaviCalcRouteListener() {
            @Override
            public void onNaviCalcRouteSuccess() {
                Log.d(TAG, "navigate calculate route success");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onNaviCalcRouteFail(WalkRoutePlanError error) {
                Log.d(TAG, "navigate calculate fail because " + error.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != getPackageManager().PERMISSION_GRANTED){
                            Log.d(TAG, "request " + permissions[i] + " permission fail");
                        }else{
                            Log.d(TAG, "request " + permissions[i] + " permission success");
                        }
                    }
                } else {
                    Log.d(TAG, "request permissions fail");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
