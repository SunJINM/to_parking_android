package sun.project.toparking.activity.baidu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.route.RoutePlanSearch;

import sun.project.toparking.R;
import sun.project.toparking.activity.baidu.listener.MyLocationListener;
import sun.project.toparking.activity.baidu.util.BaiduMapUtils;
import sun.project.toparking.util.LocationUtil;

public class BaiduMapActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient = null;
    private TextView getProvinceA;
    private TextView getCityA;
    private TextView getAddressA;
    private TextView getLongitudeA;
    private TextView getLatitudeA;
//    private TextView getLocButtonA;
//    private Button backConsole;

    public void init(){
        getProvinceA=(TextView) findViewById(R.id.get_province_a);
        getCityA=(TextView) findViewById(R.id.get_city_a);
        getAddressA=(TextView) findViewById(R.id.get_detail_address_a);
        getLongitudeA=(TextView) findViewById(R.id.get_longitude_a);
        getLatitudeA=(TextView) findViewById(R.id.get_latitude_a);
//        getLocButtonA=(TextView) findViewById(R.id.get_location_a);
//        backConsole=(Button) findViewById(R.id.back_console);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        } else {
            BaiduMapUtils.initSDKFirst(this,true);
            setContentView(R.layout.baidu_map);
            try {
//                init();
//                //监听获取按钮
//                getLocButtonA.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        LocationUtil locationUtil1 = new LocationUtil();
//                        //获取位置
//                        Address address1 = locationUtil1.getALocation(BaiduMapActivity.this);
//                        //获取省份
//                        String province1 = address1.getAdminArea();
//                        //获取城市
//                        String city1 = address1.getLocality();
//                        //获取地址
//                        String detailAddress1 = address1.getAddressLine(0);
//                        //获取经度
//                        String longitude1 = String.valueOf(address1.getLongitude());
//                        //获取纬度
//                        String latitude1 = String.valueOf(address1.getLatitude());
//                        System.out.println("重新获取位置中！");
//                        getProvinceA.setText(province1);
//                        getCityA.setText(city1);
//                        getAddressA.setText(detailAddress1);
//                        getLongitudeA.setText(longitude1);
//                        getLatitudeA.setText(latitude1);
//                    }
//                });

                initmap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "没有ACCESS_FINE_LOCATION权限！", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case 2:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "没有ACCESS_COARSE_LOCATION权限！", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case 3:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "没有WRITE_EXTERNAL_STORAGE权限！", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
        BaiduMapUtils.initSDKFirst(this,true);
        setContentView(R.layout.baidu_map);
        try {
//            init();
//            //监听获取按钮
//            getLocButtonA.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    LocationUtil locationUtil1 = new LocationUtil();
//                    //获取位置
//                    Address address1 = locationUtil1.getALocation(BaiduMapActivity.this);
//                    //获取省份
//                    String province1 = address1.getAdminArea();
//                    //获取城市
//                    String city1 = address1.getLocality();
//                    //获取地址
//                    String detailAddress1 = address1.getAddressLine(0);
//                    //获取经度
//                    String longitude1 = String.valueOf(address1.getLongitude());
//                    //获取纬度
//                    String latitude1 = String.valueOf(address1.getLatitude());
//                    System.out.println("重新获取位置中！");
//                    getProvinceA.setText(province1);
//                    getCityA.setText(city1);
//                    getAddressA.setText(detailAddress1);
//                    getLongitudeA.setText(longitude1);
//                    getLatitudeA.setText(latitude1);
//                }
//            });
            initmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 地图初始化
     */
    private void initmap() throws Exception {
        init();
        LocationUtil locationUtil = new LocationUtil();
        //获取位置
        Address address = locationUtil.getALocation(BaiduMapActivity.this);
        //获取省份
        String province = address.getAdminArea();
        //获取城市
        String city = address.getLocality();
        //获取地址
        String detailAddress = address.getAddressLine(0);
        //获取经度
        String longitude = String.valueOf(address.getLongitude());
        //获取纬度
        String latitude = String.valueOf(address.getLatitude());
        System.out.println("获取位置并给组件赋值！");
        getProvinceA.setText(province);
        getCityA.setText(city);
        getAddressA.setText(detailAddress);
        getLongitudeA.setText(longitude);
        getLatitudeA.setText(latitude);
        //定位初始化
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener(mMapView,mBaiduMap));
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //开启地图的定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener(mMapView,mBaiduMap);
        mLocationClient.registerLocationListener(myLocationListener);
        //initOrientationListener();
        //开启地图定位图层
        mLocationClient.start();
        //myOrientationListener.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

}
