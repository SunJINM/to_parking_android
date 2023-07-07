package sun.project.toparking.activity.baidu.listener;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

//构造地图数据
public class MyLocationListener extends BDAbstractLocationListener {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    public MyLocationListener() {
    }
    private boolean isFirstLoc=true;
    private boolean autoLocation=false;
    public void setAutoLocation(boolean b){
        autoLocation = b;

    }

    public MyLocationListener(MapView mMapView, BaiduMap mBaiduMap) {
        this.mMapView = mMapView;
        this.mBaiduMap = mBaiduMap;
    }

    @Override
    public void onReceiveLocation(BDLocation bdlocation) {

        //mapView 销毁后不在处理新接收的位置
        if (bdlocation == null || mMapView == null){
            return;
        }

        LatLng latLng = new LatLng(bdlocation.getLatitude(), bdlocation.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdlocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(bdlocation.getDirection()).latitude(bdlocation.getLatitude())
                .longitude(bdlocation.getLongitude()).build();
        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, null );
        mBaiduMap.setMyLocationConfiguration(configuration);
        //自动返回定位点
        //mBaiduMap.animateMapStatus(update);
        mBaiduMap.setMyLocationData(locData);

        /**
         *当首次定位或手动发起定位时，记得要放大地图，便于观察具体的位置
         * LatLng是缩放的中心点，这里注意一定要和上面设置给地图的经纬度一致；
         * MapStatus.Builder 地图状态构造器
         */
        if (isFirstLoc||autoLocation) {
            isFirstLoc = false;
            autoLocation=false;
            LatLng ll = new LatLng(bdlocation.getLatitude(), bdlocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            //设置缩放中心点；缩放比例；
            builder.target(ll).zoom(18.0f);
            //给地图设置状态
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }


    }
}
