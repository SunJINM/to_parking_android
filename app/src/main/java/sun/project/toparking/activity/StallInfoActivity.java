package sun.project.toparking.activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.activity.baidu.util.MapUtil;
import sun.project.toparking.util.BitmapUtils;
import sun.project.toparking.util.CommonUtil;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.SharedPreferencesUtil;

/**
 * @describe 停车位信息活动页面处理类
 */
public class StallInfoActivity extends AppCompatActivity {
    private TextView getProvince;
    private TextView getCity;
    private TextView getAddress;
    private TextView getLongitude;
    private TextView getLatitude;
    //停车位照片
    private ImageView getStallImage;
    private TextView getStallState;

    //导航按钮
    private TextView buttonNavigate;
    //预约按钮
    private TextView buttonBook;
    //经纬度
    private Double navigateLongitude;
    private Double navigateLatitude;
    //地址
    private String navigateAddress;
    //类型信息
    private TextView stallType;
    //
    private TextView stallPrice;
    //用户Id
    private String getUserId;
    //停车位Id
    private String getStallId;
    /**
     * 组件初始化
     */
    public void init(){
        getProvince=(TextView) findViewById(R.id.stall_info_get_province);
        getCity=(TextView) findViewById(R.id.stall_info_get_city);
        getAddress=(TextView) findViewById(R.id.stall_info_get_detail_address);
        getLongitude=(TextView) findViewById(R.id.stall_info_get_longitude);
        getLatitude=(TextView) findViewById(R.id.stall_info_get_latitude);
        getStallImage=(ImageView) findViewById(R.id.stall_info_get_stall_image);
        getStallState=(TextView) findViewById(R.id.stall_info_is_book);
        stallType=(TextView) findViewById(R.id.stall_info_get_detail_type);
        stallPrice=(TextView) findViewById(R.id.stall_info_is_price);
        buttonBook=(TextView) findViewById(R.id.stall_book);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stall_info);
        //初始化组件
        init();
        getUserId=SharedPreferencesUtil.getUserId(MainActivity.getContext());
        //赋值
        System.out.println("停车位信息展现！");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取车位详情
                String url="http://121.43.101.84:9000/to_parking/stall/getStallById/";
                Intent intent=StallInfoActivity.this.getIntent();
                String stallId=intent.getStringExtra("stallId");
                getStallId=stallId;
                try {
                    System.out.println("访问的接口："+url+stallId);
                    JSONObject jsonObject = HttpGetPostUtil.doGet(url+stallId).getJSONObject("data");
                    //获取各个信息
                    String imagePath=jsonObject.getString("url");
                    Bitmap stallImage= BitmapUtils.displayImage(imagePath);
                    String province=jsonObject.getString("province");
                    String city=jsonObject.getString("city");
                    String address=jsonObject.getString("address");
                    StallInfoActivity.this.navigateAddress=address;
                    String longitude=jsonObject.getString("longitude");
                    StallInfoActivity.this.navigateLongitude= Double.valueOf(longitude);
                    String latitude=jsonObject.getString("latitude");
                    StallInfoActivity.this.navigateLatitude= Double.valueOf(latitude);
                    boolean isBook=jsonObject.getBoolean("isBook");
                    String bookState="空闲";
                    String type=jsonObject.getString("category");
                    String price=jsonObject.getString("price")+"元/小时";
                    //给组件赋值
                    runOnUiThread(()->{
                        getProvince.setText(province);
                        getCity.setText(city);
                        getAddress.setText(address);
                        getLongitude.setText(longitude);
                        getLatitude.setText(latitude);
                        if(!imagePath.equals("#") && !TextUtils.isEmpty(imagePath)){
                            getStallImage.setImageBitmap(stallImage);
                        }

                        getStallState.setText(bookState);
                        stallType.setText(type);
                        stallPrice.setText(price);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //获取各个信息
            }
        }).start();
        //监听停车按钮
        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //建立线程访问url
                new Thread(()->{

                    String url="http://121.43.101.84:9000/to_parking/stallUse/addStallUse";
                    System.out.println(url);
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("userId",getUserId);
                    hashMap.put("stallId",getStallId);
                    JSONObject jsonObject = HttpGetPostUtil.doPost(url,hashMap);
                    int status=0;
                    boolean verify=true;
                    try {
                        status=jsonObject.getInt("status");
                        verify=jsonObject.getJSONObject("data").getBoolean("verify");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(status==200 && verify==true){
                        runOnUiThread(()->{
                            //预约成功
                            AlertDialog.Builder builder = new AlertDialog.Builder(StallInfoActivity.this);
                            builder.setTitle("温馨提示！");
                            builder.setMessage("您已停车成功，请点击确定返回！");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(StallInfoActivity.this, "已返回！", Toast.LENGTH_SHORT).show();//创建一个警告对话框
                                    //预约成功要刷新页面
                                    finish();
                                }
                            }).setIcon(R.drawable.re_get);
                            AlertDialog alertDialog =builder.create();//这个方法可以返回一个alertDialog对象
                            alertDialog.show();
                        });
                    }
                    if(status!=200 && verify==false){
                        //预约失败，你有预约车位流程还在进行中
                        runOnUiThread(()->{
                            //预约成功
                            AlertDialog.Builder builder = new AlertDialog.Builder(StallInfoActivity.this);
                            builder.setTitle("温馨提示！");
                            builder.setMessage("停车失败！已停车！请点击退出！");
                            builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(StallInfoActivity.this, "已返回！", Toast.LENGTH_SHORT).show();//创建一个警告对话框
                                }
                            }).setIcon(R.drawable.re_get);
                            AlertDialog alertDialog =builder.create();//这个方法可以返回一个alertDialog对象
                            alertDialog.show();
                        });
                    }
                }).start();
            }

        });
        //监听导航按钮
       /* buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog= CommonUtil.appear(StallInfoActivity.this,R.layout.map_menu);
                //监听选择的按钮
                dialog.findViewById(R.id.gaodeMap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("StallInfoActivity", "百度地图！");
                        //获取经纬度
                        System.out.println("获取经度："+navigateLongitude);
                        System.out.println("获取纬度："+navigateLatitude);
                        System.out.println("导航的目的地："+navigateAddress);
                        dialog.dismiss();
                        if (MapUtil.isBaiduMapInstalled()){
                            MapUtil.openBaiDuNavi(StallInfoActivity.this, 0, 0, null,  navigateLatitude,navigateLongitude, navigateAddress);
                        } else {
                            Toast.makeText(StallInfoActivity.this, "尚未安装百度地图", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //监听选择的按钮
                dialog.findViewById(R.id.baiduMap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("StallInfoActivity", "高德地图！");
                        dialog.dismiss();
                        if (MapUtil.isGdMapInstalled()) {
                            //获取经纬度
                            System.out.println("获取经度："+navigateLongitude);
                            System.out.println("获取纬度："+navigateLatitude);
                            System.out.println("导航的目的地："+navigateAddress);
                            MapUtil.openGaoDeNavi(StallInfoActivity.this, 0, 0, null,  navigateLatitude,navigateLongitude, navigateAddress);
                        } else {
                            //这里必须要写逻辑，不然如果手机没安装该应用，程序会闪退，这里可以实现下载安装该地图应用
                            Toast.makeText(StallInfoActivity.this, "尚未安装高德地图", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //监听选择的按钮
                dialog.findViewById(R.id.tengxunMap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("StallInfoActivity", "腾讯地图！");
                        dialog.dismiss();
                        if (MapUtil.isTencentMapInstalled()) {
                            //获取经纬度
                            System.out.println("获取经度："+navigateLongitude);
                            System.out.println("获取纬度："+navigateLatitude);
                            System.out.println("导航的目的地："+navigateAddress);
                            MapUtil.openTencentMap(StallInfoActivity.this, 0, 0, null,  navigateLatitude,navigateLongitude, navigateAddress);
                        } else {
                            //这里必须要写逻辑，不然如果手机没安装该应用，程序会闪退，这里可以实现下载安装该地图应用
                            Toast.makeText(StallInfoActivity.this, "尚未安装腾讯地图", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });*/
    }
}
