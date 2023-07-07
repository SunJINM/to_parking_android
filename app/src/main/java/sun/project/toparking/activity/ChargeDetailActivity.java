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
import sun.project.toparking.alipay.AliPayActivity;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.TimeUtil;

public class ChargeDetailActivity extends AppCompatActivity {

    private TextView bookType;
    private TextView bookProvince;
    private TextView bookCity;
    private TextView bookAddress;
    private TextView bookLongitude;
    private TextView bookLatitude;
    private TextView bookPrice;

    private TextView bookGmtCreate;
    private TextView bookCancelTime;
    private TextView bookCancel;


    private TextView stopTotalPrice;
    private String userId;
    private String id;

    public void init() {
        bookType = (TextView) findViewById(R.id.book_type);
        bookProvince = (TextView) findViewById(R.id.book_province);
        bookCity = (TextView) findViewById(R.id.book_city);
        bookAddress = (TextView) findViewById(R.id.book_address);
        bookLongitude = (TextView) findViewById(R.id.book_longitude);
        bookLatitude = (TextView) findViewById(R.id.book_latitude);
        bookPrice = (TextView) findViewById(R.id.book_is_price);
        bookGmtCreate = (TextView) findViewById(R.id.book_gmt_create);
        bookCancelTime = (TextView) findViewById(R.id.book_end_time);
        bookCancel=(TextView)findViewById(R.id.cancel_book);
        stopTotalPrice = (TextView)findViewById(R.id.total_price);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_detail);

        init();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        userId = intent.getStringExtra("userId");
        System.out.println("显示的订单号为：" + id);
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
            String userId = result.getString("userId");
            String bookTypeValue = result.getString("category");
            String bookProvinceValue = result.getString("province");
            String bookCityValue = result.getString("city");
            String bookAddressValue = result.getString("address");
            String bookLongitudeValue = result.getString("longitude");
            String bookLatitudeValue = result.getString("latitude");
            String useEndTimeValue = result.getString("useEndTime");
            String useStartTimeValue = result.getString("useStartTime");
            String bookPriceValue = result.getInt("price")+"元/小时";
            int price = result.getInt("totalPrice");
            String totalPrice = price+"元";

            //给组件赋值
            String finalStartTimeValue = useStartTimeValue;
            String finalStopTimeValue = useEndTimeValue;
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
                    bookPrice.setText(bookPriceValue);
                    bookGmtCreate.setText(finalStartTimeValue);
                    bookCancelTime.setText(finalStopTimeValue);
                    stopTotalPrice.setText(totalPrice);
                    //监听按钮

                    bookCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ChargeDetailActivity.this, AliPayActivity.class);
                            intent.putExtra("price", price);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    });


                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}