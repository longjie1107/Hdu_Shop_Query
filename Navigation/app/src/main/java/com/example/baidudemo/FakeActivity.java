package com.example.baidudemo;

import android.app.Activity;
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
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorPlanNode;
import com.baidu.mapapi.search.route.IndoorRoutePlanOption;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;
import java.util.List;

public class FakeActivity extends Activity {
    private static final int PERMISSIONS_REQUEST = 1;
    private static final String TAG = "FakeActivity";
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
    }

    private void routePlanWithRouteNode() {
        RoutePlanSearch mSearch = RoutePlanSearch.newInstance();
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
                //创建IndoorRouteOverlay实例
                IndoorRouteOverlay overlay = new IndoorRouteOverlay(mBaiduMap);
                if (indoorRouteResult.getRouteLines() != null && indoorRouteResult.getRouteLines().size() > 0) {
                    //获取室内路径规划数据（以返回的第一条路线为例）
                    //为IndoorRouteOverlay实例设置数据
                    overlay.setData(indoorRouteResult.getRouteLines().get(0));
                    //在地图上绘制IndoorRouteOverlay
                    overlay.addToMap();
                }
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        };
        mSearch.setOnGetRoutePlanResultListener(listener);
        IndoorPlanNode startNode = new IndoorPlanNode(new LatLng(39.917380, 116.37978), "F1");
        IndoorPlanNode endNode = new IndoorPlanNode(new LatLng(39.917239, 116.37955), "F6");
        mSearch.walkingIndoorSearch(new IndoorRoutePlanOption()
                .from(startNode)
                .to(endNode));
        mSearch.destroy();
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
