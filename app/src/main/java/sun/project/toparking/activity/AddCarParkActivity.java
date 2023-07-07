package sun.project.toparking.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.LocationUtil;

public class AddCarParkActivity extends AppCompatActivity implements View.OnClickListener {



    //之前的
    private TextView buttonGetLocation;
    private TextView getProvince;
    private TextView getCity;
    private TextView getAddress;
    private TextView getLongitude;
    private TextView getLatitude;
    private TextView getCarParkName;

    //停车位信息上传按钮
    private Button buttonUpload;


    private TextView getCarParkId;


    public void init(){
        buttonGetLocation=(TextView) findViewById(R.id.get_location);
        getProvince=(TextView) findViewById(R.id.get_province);
        getCity=(TextView) findViewById(R.id.get_city);
        getAddress=(TextView) findViewById(R.id.get_detail_address);
        getLongitude=(TextView) findViewById(R.id.get_longitude);
        getLatitude=(TextView) findViewById(R.id.get_latitude);
        buttonUpload=(Button) findViewById(R.id.upload_carPark);
        getCarParkName=(TextView) findViewById(R.id.get_car_park_name);
        getCarParkId = (TextView)findViewById(R.id.get_carPark_id);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car_park);
        init();
        //获取定位事件监听
        buttonGetLocation.setOnClickListener(this);
        //监控停车场信息上传
        buttonUpload.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        System.out.println("监听按钮事件：" + view);
        int id = view.getId();
        switch (id) {
            //获取定位
            case R.id.get_location: {
                LocationUtil locationUtil = new LocationUtil();
                //获取位置
                Address address = locationUtil.getALocation(AddCarParkActivity.this);
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
                buttonGetLocation.setText("重新获取");
                runOnUiThread(() -> {
                    //渲染组件
                    getProvince.setText(province);
                    getCity.setText(city);
                    getAddress.setText(detailAddress);
                    getLongitude.setText(longitude);
                    getLatitude.setText(latitude);
                });
                break;
            }
            //上传定位
            case R.id.upload_carPark: {
                Log.d("upload_stall", "onClick: 上传停车位详细信息！");
                new Thread(() -> {

                    //获取省份
                    String province = getProvince.getText().toString().trim();
                    //获取城市
                    String city = getCity.getText().toString().trim();
                    //获取地址
                    String detailAddress = getAddress.getText().toString().trim();
                    //获取经度
                    String longitude = getLongitude.getText().toString().trim();
                    //获取纬度
                    String latitude = getLatitude.getText().toString().trim();
                    //获取停车场ID
                    String carParkId = getCarParkId.getText().toString().trim();
                    //获取类型
                    String carParkName = getCarParkName.getText().toString().trim();

                    //以上信息均不能为空
                    if (TextUtils.isEmpty(province) || TextUtils.isEmpty(city) || TextUtils.isEmpty(detailAddress)
                            || TextUtils.isEmpty(longitude) || TextUtils.isEmpty(latitude)
                            || TextUtils.isEmpty(carParkName)
                    ) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddCarParkActivity.this, "请先定位车位位置!且完善车位信息", Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        //将信息保存至接口
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("province", province);
                        hashMap.put("city", city);
                        hashMap.put("address", detailAddress);
                        hashMap.put("longitude", longitude);
                        hashMap.put("latitude", latitude);
                        hashMap.put("carParkId", carParkId);
                        hashMap.put("carParkName", carParkName);


                        String url = "http://121.43.101.84:9000/to_parking/carPark/addCarPark";
                        HttpGetPostUtil.doPost(url, hashMap);
                        runOnUiThread(() -> {
                            Toast.makeText(AddCarParkActivity.this, "已上传！", Toast.LENGTH_SHORT).show();//创建一个警告对话框
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("温馨提示！");
                            builder.setMessage("停车位信息上传已完成，请点击退出！");
                            AlertDialog.Builder builder1 = builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(AddCarParkActivity.this, "已退出！", Toast.LENGTH_SHORT).show();//创建一个警告对话框
                                    Intent intent = new Intent(AddCarParkActivity.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });

                            AlertDialog alertDialog = builder.create();//这个方法可以返回一个alertDialog对象
                            alertDialog.show();
                        });
                    }
                }).start();
                break;
            }
        }
    }

}