package sun.project.toparking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import sun.project.toparking.R;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.TimeUtil;

public class CarDetailActivity extends AppCompatActivity {

    private TextView bookType;
    private TextView bookProvince;
    private TextView bookCity;
    private TextView bookAddress;
    private TextView bookLongitude;
    private TextView bookLatitude;
    private TextView bookState;
    private TextView bookPrice;

    private TextView bookGmtCreate;

    private TextView bookCancel;


    private TextView stopTotalPrice;
    public void init() {
        bookType = (TextView) findViewById(R.id.book_type);
        bookProvince = (TextView) findViewById(R.id.book_province);
        bookCity = (TextView) findViewById(R.id.book_city);
        bookAddress = (TextView) findViewById(R.id.book_address);
        bookLongitude = (TextView) findViewById(R.id.book_longitude);
        bookLatitude = (TextView) findViewById(R.id.book_latitude);
        bookState = (TextView) findViewById(R.id.book_state);
        bookPrice = (TextView) findViewById(R.id.book_is_price);
        bookGmtCreate = (TextView) findViewById(R.id.book_gmt_create);
        bookCancel=(TextView)findViewById(R.id.cancel_book);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        init();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String userId = intent.getStringExtra("userId");
        System.out.println("显示的预约信息订单号为：" + id);
        new Thread(() -> {
            String url = "http://121.43.101.84:9000/to_parking/stallUse/getStallStopInfoById/";
            //执行url
            System.out.println(url + id);
            JSONObject jsonObject = HttpGetPostUtil.doGet(url + id);
            getStallInfo(jsonObject);
        }).start();
    }
    @SuppressLint("ResourceAsColor")
    public void getStallInfo(JSONObject jsonObject) {
        JSONObject result=null;
        try {
            result=jsonObject.getJSONObject("data");

            String bookTypeValue = result.getString("category");
            String bookProvinceValue = result.getString("province");
            String bookCityValue = result.getString("city");
            String bookAddressValue = result.getString("address");
            String bookLongitudeValue = result.getString("longitude");
            String bookLatitudeValue = result.getString("latitude");
            boolean isUse = result.getBoolean("isUse");
            boolean stopUse = result.getBoolean("stopUse");
            String bookStateValue="停车中";
            String useEndTimeValue = "-";
            if(isUse==false && stopUse==true){
                bookStateValue="停车已结束";
                useEndTimeValue = result.getString("useEndTime");

            }
            String bookPriceValue = result.getInt("price")+"元/小时";
            String bookOrderValue = result.getString("id");
            String bookGmtCreateValue = result.getString("gmtCreate");
            //给组件赋值
            String finalBookStateValue = bookStateValue;
            String finalBookCancelTimeValue = useEndTimeValue;
            JSONObject finalResult = result;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bookType.setText(bookTypeValue);
                    bookProvince.setText(bookProvinceValue);
                    bookCity.setText(bookCityValue);
                    bookAddress.setText(bookAddressValue);
                    bookLongitude.setText(bookLongitudeValue);
                    bookLatitude.setText(bookLatitudeValue);
                    bookState.setText(finalBookStateValue);
                    bookPrice.setText(bookPriceValue);
                    bookGmtCreate.setText(bookGmtCreateValue);
                    //监听按钮
                    if(isUse == true && stopUse==false) {
                        runOnUiThread(() -> {
                            bookCancel.setText("结束停车");
                        });
                        String stallId = null;
                        try {
                            stallId = finalResult.getString("stallId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String getUserId = null;
                        try {
                            getUserId = finalResult.getString("userId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //监控按钮
                        String finalStallId = stallId;
                        String finalGetUserId = getUserId;
                        bookCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new Thread(()->{
                                    String cancelUrl = "http://121.43.101.84:9000/to_parking/stallUse/cancelStop?stallId=" + finalStallId + "&userId=" + finalGetUserId;
                                    System.out.println("cancelUrl\n"+cancelUrl);
                                    HttpGetPostUtil.doGet(cancelUrl);
                                }).start();

                                runOnUiThread(() -> {
                                    bookCancel.setText("已结束");
                                    String time= TimeUtil.getStandardTimeToSecond();
                                    Toast.makeText(CarDetailActivity.this, "已结束停车！", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });

                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}