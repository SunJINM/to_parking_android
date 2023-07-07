package sun.project.toparking.activity.baidu.util;

import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

/**
 *
 */
public class BaiduMapUtils {
    /**
     * 初始化百度地图，最简单的初始化
     * @param context
     * @param status
     */
    public static void initSDKFirst(Context context, boolean status) {
        LocationClient.setAgreePrivacy(status);
        try {
            //注意看报错信息
            SDKInitializer.setAgreePrivacy(context.getApplicationContext(),true);
            //在使用SDK各组件之前初始化context信息，传入ApplicationContext
            SDKInitializer.initialize(context.getApplicationContext());
            //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
            //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
            SDKInitializer.setCoordType(CoordType.BD09LL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

