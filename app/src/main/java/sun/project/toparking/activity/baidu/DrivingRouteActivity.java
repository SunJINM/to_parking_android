package sun.project.toparking.activity.baidu;

import static android.widget.AbsListView.*;
import static com.baidu.mapapi.map.BaiduMap.MAP_TYPE_NONE;
import static com.baidu.mapapi.map.BaiduMap.MAP_TYPE_NORMAL;
import static com.baidu.mapapi.map.BaiduMap.MAP_TYPE_SATELLITE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextParams;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.List;

import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.activity.baidu.adapter.PoiAdapter;
import sun.project.toparking.activity.baidu.listener.MyLocationListener;
import sun.project.toparking.activity.baidu.util.overlayutil.DrivingRouteOverlay;
import sun.project.toparking.util.LocationUtil;

public class DrivingRouteActivity extends AppCompatActivity implements View.OnClickListener{


    private  RoutePlanSearch routePlanSrch=null;
    private  BDLocation mCurLocation = null;

    private PoiSearch mPoiSearch=null;
    //创建mapView类型成员变量
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    private LocationClient mLocationClient=null;
    private MyLocationListener myLocationListener;
    private EditText mInputText;

    private LatLng enLatLng = null;
    private String carParkAddress=null;

    private String parkingAddress;
    private String Double;
    private LatLng stLatLng=null;

    private String parkName = null;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_route);


        LocationClient.setAgreePrivacy(true);
        LocationUtil locationUtil = new LocationUtil();
        Address address = locationUtil.getALocation(DrivingRouteActivity.this);




        //创建路径规划实例
        routePlanSrch = RoutePlanSearch.newInstance();

        //设置路线规划检索监听器
        routePlanSrch.setOnGetRoutePlanResultListener(routePlanSrchlistener);

        Intent intent = DrivingRouteActivity.this.getIntent();

        //共同需要，起点LatLng, 终点LatLng

        //起点，手机所在地，本人位置
        LocationUtil locationUtil1 = new LocationUtil();
        Address address1 = locationUtil1.getALocation(DrivingRouteActivity.this);
        stLatLng = new LatLng(address1.getLatitude(), address1.getLongitude());

        //终点
        enLatLng = intent.getParcelableExtra("enLatLng");

        //目的地名字
        parkName = intent.getStringExtra("parkName");
        carParkAddress = intent.getStringExtra("carParkAddress");

        System.out.println(parkName);
        System.out.println(carParkAddress);

        if(parkName == null){
            parkingAddress = carParkAddress + "停车场";
            carParkAddress = null;
        }else{
            parkingAddress = parkName;
            parkName = null;
        }

        System.out.println(parkingAddress);

        Double distance = DistanceUtil.getDistance(stLatLng, enLatLng);
        int dist = (int) Math.round(distance);

        TextView dis = findViewById(R.id.distance);
        String d = dist + "m";
        dis.setText(d);

        PlanNode stNode = PlanNode.withLocation(stLatLng);
        PlanNode enNode = PlanNode.withLocation(enLatLng);
        System.out.println(stNode);
        System.out.println(enNode);
        routePlanSrch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));

        findViewById(R.id.go_baidu).setOnClickListener(this);
        //定位初始化
        try {

            mLocationClient = new LocationClient(getApplicationContext());
            mLocationClient.registerLocationListener(new MyLocationListener(mMapView,mBaiduMap));
            //获取地图控件引用
            mMapView = (MapView) findViewById(R.id.bmapView);
            //开启定位
            mBaiduMap = mMapView.getMap();

            mBaiduMap.setMyLocationEnabled(true);

            mLocationClient = new LocationClient(this);
            //通过LocationClientOption设置LocationClient相关参数
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setIsNeedAddress(true);
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            //设置locationClientOption
            mLocationClient.setLocOption(option);

            myLocationListener = new MyLocationListener(mMapView, mBaiduMap);
            mLocationClient.registerLocationListener(myLocationListener);
            //注册LocationListener监听器
            //开启地图定位图层
            mLocationClient.start();

            findViewById(R.id.mapTypeBtn).setOnClickListener(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //创建路线规划检索结果监听器
    OnGetRoutePlanResultListener routePlanSrchlistener = new OnGetRoutePlanResultListener() {
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
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            //创建DrivingRouteOverlay实例
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mMapView.getMap());
            List<DrivingRouteLine> routelines=drivingRouteResult.getRouteLines();
            if(routelines!=null&&routelines.size()>0)
            {
                for(DrivingRouteLine routeLine:routelines
                ) {
                    //为DrivingRouteOverlay实例设置数据
                    overlay.setData(routeLine);
                    //在地图上绘制DrivingRouteOverlay
                    overlay.addToMap();

                }

                //a关闭搜索结果

            }
        }
    };



    @Override
    protected void onResume(){
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected  void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        /*mLocationClient.stop();
        mMapView.getMap().setMyLocationEnabled(false);*/
        mMapView.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mapTypeBtn:
                BaiduMap map= mMapView.getMap();
                int type=map.getMapType();
                switch(type)
                {
                    case MAP_TYPE_NORMAL:
                        map.setMapType( MAP_TYPE_SATELLITE);//从普通地图切换到卫星地图
                        break;
                    case MAP_TYPE_SATELLITE:
                        map.setMapType(MAP_TYPE_NORMAL);//从卫星地图切换到空白地图
                        break;
                }
                break;
            case R.id.go_baidu:
                Intent intent = new Intent();
                String uri = "baidumap://map/navi?location=" + enLatLng + "&query=" + parkingAddress + "&src=andr.baidu.openAPIdemo";
                intent.setData(Uri.parse(uri));
                startActivity(intent);

                break;
        }


    }

}
