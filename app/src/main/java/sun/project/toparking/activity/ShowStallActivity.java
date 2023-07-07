package sun.project.toparking.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.project.toparking.R;
import sun.project.toparking.activity.baidu.DrivingRouteActivity;
import sun.project.toparking.util.HttpGetPostUtil;

public class ShowStallActivity extends AppCompatActivity implements View.OnClickListener {

    private String address;
    private LatLng enLatLng;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stall);
        Intent intent = ShowStallActivity.this.getIntent();

        address = intent.getStringExtra("carParkName");
        System.out.println("------------------"+address);
        enLatLng = intent.getParcelableExtra("enLatLng");
        findViewById(R.id.carPark_daoHang).setOnClickListener(this);
        new Thread(()->{
            String carParkId = intent.getStringExtra("carParkId");
            System.out.println(carParkId);
            String url="http://121.43.101.84:9000/to_parking/stall/getAllStallByCarParkId/";
            JSONObject jsonObject = HttpGetPostUtil.doGet(url+carParkId);
            System.out.println(jsonObject);
            getStallInfo(jsonObject);


        }).start();
    }

    @SuppressLint("ResourceAsColor")
    public void getStallInfo(JSONObject jsonObject){
        Log.d("dynamic","建立线程访问获取动态数据接口的页面，然后控制控件");
        //访问url,获取数据
        Log.d("dynamic","获取到的用户动态数据为：\n"+jsonObject);
        //            //创建一个List集合，通过for循环将文字数组放到Map中，并添加到list集合中。
        List<HashMap<String,Object>> hashMapList=new ArrayList<>();
        try {
            JSONArray stallArray=jsonObject.getJSONArray("data");
            System.out.println(stallArray);
            //映射组件
            for (int i = 0; i < stallArray.length(); i++) {
                //获取动态数据
                JSONObject stallJsonObject= (JSONObject) stallArray.get(i);
                Log.d("dynamic","获取到的一个动态数据为：\n"+stallJsonObject);
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("address",stallJsonObject.getString("address"));
                hashMap.put("stallId",stallJsonObject.getString("stallId"));
                hashMap.put("category",stallJsonObject.getString("category"));
                String price=stallJsonObject.getString("price");
                price=price+"元/小时";
                hashMap.put("price",price);
                hashMap.put("province",stallJsonObject.getString("province"));
                hashMap.put("city",stallJsonObject.getString("city"));
                hashMap.put("longitude",stallJsonObject.getString("longitude"));
                hashMap.put("latitude",stallJsonObject.getString("latitude"));
                //获取是否预约信息
                boolean isBook=stallJsonObject.getBoolean("isBook");
                String isBookText="空闲";
                if(isBook){
                    isBookText="已被使用";
                }
                hashMap.put("isBook",isBookText);
                //获取图片链接
                hashMapList.add(hashMap);
            }
            /**
             * 映射的数据为
             */
            System.out.println(hashMapList);
            //创建一个适配器，将适配器与ListView关联，为ListView创建监听事件，
            // 然后通过getItemAlPosition()方法获取选中的值，最好通过Toast.
            // makeText()方法获取的值显示出来
            runOnUiThread(()->{
                System.out.println("创建一个适配器，将适配器与ListView关联，为ListView创建监听事件");
                SimpleAdapter adapter=new SimpleAdapter(
                        this,
                        hashMapList,
                        R.layout.stall_list,
                        new String[]{"category","address","isBook","price"},
                        new int[]{
                                R.id.console_type,
                                R.id.console_address,
                                R.id.console_is_book,
                                R.id.user_charge
                        });
                //将适配器与ListView关联
                ListView listView=findViewById(R.id.list_my_book);
                listView.setAdapter(adapter);
                Log.d("stall_info","遍历完成！");
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //添加动态数据点击事件
        ListView listView=findViewById(R.id.list_my_book);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择项
                Map<String,Object> map=(Map<String, Object>)adapterView.getItemAtPosition(i);
                String isBook=map.get("isBook").toString();
                if(isBook.equals("已停车")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowStallActivity.this);
                    builder.setTitle("温馨提示！");
                    builder.setMessage("停车位已被使用，请点击返回！");
                    builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(ShowStallActivity.this, "已返回！", Toast.LENGTH_SHORT).show();//创建一个警告对话框
                        }
                    }).setIcon(R.drawable.re_get);
                    AlertDialog alertDialog =builder.create();//这个方法可以返回一个alertDialog对象
                    alertDialog.show();
                }else {
                    //跳转到详情页
                    String stallId=map.get("stallId").toString();
                    Log.d("TAG", "onItemClick: 工具Id跳转到详情页"+stallId);
                    Intent intent=new Intent(ShowStallActivity.this,StallInfoActivity.class);
                    //将车位信息Id进行传输
                    intent.putExtra("stallId",stallId);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(ShowStallActivity.this, DrivingRouteActivity.class);

        intent.putExtra("enLatLng", enLatLng);
        intent.putExtra("carParkAddress", address);
        startActivity(intent);
    }
}