package sun.project.toparking.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 位置信息处理
 */
public class LocationUtil {
    /**
     * 获取一次location信息
     */
    public Address getALocation(Context context){
        //获取系统的LocationManager对象
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        //添加权限检查
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        //设置每一秒获取一次location信息
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,      //GPS定位提供者
                10000,       //更新数据时间为1秒
                2,      //位置间隔为1米
                //位置监听器
                new LocationListener() {  //GPS定位信息发生改变时触发，用于更新位置信息
                    @Override
                    public void onLocationChanged(Location location) {
                        //GPS信息发生改变时，更新位置
                        getAddress(context,location);
                    }

                    @Override
                    //位置状态发生改变时触发
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    //定位提供者启动时触发
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    //定位提供者关闭时触发
                    public void onProviderDisabled(String provider) {
                    }
                });
        //从GPS获取最新的定位信息
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return getAddress(context,location);
    }
    /**
     * 设置每一秒获取一次location信息
     */
    public void getDynamicLocation(Context context){
        //获取系统的LocationManager对象
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        //添加权限检查
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //设置每一秒获取一次location信息
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,      //GPS定位提供者
                1000,       //更新数据时间为1秒
                1,      //位置间隔为1米
                //位置监听器
                new LocationListener() {  //GPS定位信息发生改变时触发，用于更新位置信息

                    @Override
                    public void onLocationChanged(Location location) {
                        //GPS信息发生改变时，更新位置
                        locationUpdates(location);
                    }

                    @Override
                    //位置状态发生改变时触发
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    //定位提供者启动时触发
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    //定位提供者关闭时触发
                    public void onProviderDisabled(String provider) {
                    }
                });
        //从GPS获取最新的定位信息
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationUpdates(location);    //将最新的定位信息传递给创建的locationUpdates()方法中

    }

    public void locationUpdates(Location location) {  //获取指定的查询信息
        //如果location不为空时
        if (location != null) {
            StringBuilder stringBuilder = new StringBuilder();        //使用StringBuilder保存数据
            //获取经度、纬度、等属性值
            stringBuilder.append("您的位置信息：\n");
            stringBuilder.append("经度：");
            stringBuilder.append(location.getLongitude());
            stringBuilder.append("\n纬度：");
            stringBuilder.append(location.getLatitude());
            stringBuilder.append("\n精确度：");
            stringBuilder.append(location.getAccuracy());
            stringBuilder.append("\n高度：");
            stringBuilder.append(location.getAltitude());
            stringBuilder.append("\n方向：");
            stringBuilder.append(location.getBearing());
            stringBuilder.append("\n速度：");
            stringBuilder.append(location.getSpeed());
            stringBuilder.append("\n时间：");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH mm ss");    //设置日期时间格式
            stringBuilder.append(dateFormat.format(new Date(location.getTime())));
            System.out.println("获取的位置信息如下：\n"+stringBuilder);
        } else {
            //否则输出空信息
            System.out.println("没有获取到GPS信息");
        }
    }
    /**
     * 获取地址
     */
    //获取地址信息:城市、街道等信息
    public static Address getAddress(Context context,Location location) {
        List<Address> result = null;
        String address = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(context, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                Address addressResult=result.get(0);
                System.out.println("我的地址信息："+addressResult);
                return addressResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
