package sun.project.toparking.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.SharedPreferencesUtil;

/**
 * 我的预约处理活动类！
 */
public class MyBookActivity extends AppCompatActivity {
    private TextView myBookType;
    private TextView myBookAddress;
    private TextView myBookState;
    private TextView myBookPrice;
    private TextView myBookDetail;
    private ListView listView;

    /**
     * 初始化组件
     */
    public void init(){
        myBookType=(TextView) findViewById(R.id.my_book_type);
        myBookAddress=(TextView) findViewById(R.id.my_book_address);
        myBookState=(TextView) findViewById(R.id.my_book_state);
        myBookPrice=(TextView) findViewById(R.id.my_book_price);
        myBookDetail=(TextView) findViewById(R.id.my_book_detail);
        listView=(ListView) findViewById(R.id.list_my_book_info);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_book);
        init();
        //建立线程访问获取动态数据接口的页面，然后控制控件
        String userId= SharedPreferencesUtil.getUserId(MainActivity.getContext());
        new Thread(()->{
            String url="http://121.43.101.84:9000/to_parking/stallUse/getStallByUserId/";
            //执行url
            System.out.println(url+userId);
            JSONObject jsonObject = HttpGetPostUtil.doGet(url+userId);
            getStallInfo(jsonObject);
        }).start();

    }

    /**
     * 建立线程访问获取动态数据接口的页面，然后控制控件
     */
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
                hashMap.put("userId",stallJsonObject.getString("userId"));
                hashMap.put("id",stallJsonObject.getString("id"));
                hashMap.put("province",stallJsonObject.getString("province"));
                hashMap.put("city",stallJsonObject.getString("city"));
                hashMap.put("longitude",stallJsonObject.getString("longitude"));
                hashMap.put("latitude",stallJsonObject.getString("latitude"));
                hashMap.put("type",stallJsonObject.getString("category"));
                String gmtCreate=stallJsonObject.getString("gmtCreate");
                hashMap.put("gmtCreate",gmtCreate);
                int price=stallJsonObject.getInt("price");
                String getPrice=price+"元/小时";
                hashMap.put("price",getPrice);
                boolean isUse=stallJsonObject.getBoolean("isUse");
                boolean stopUse=stallJsonObject.getBoolean("stopUse");
                if(isUse==true && stopUse == false){
                    hashMap.put("is_end","停车中...");
                }else {
                    hashMap.put("is_end","已结束");
                }
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
                        R.layout.my_book_list,
                        new String[]{"type","address","price","is_end","gmtCreate"},
                        new int[]{
                                R.id.my_book_type,
                                R.id.my_book_address,
                                R.id.my_book_price,
                                R.id.my_book_state,
                                R.id.list_gmt_create
                        });
                //将适配器与ListView关联
                listView.setAdapter(adapter);
                Log.d("stall_info","遍历完成！");
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择项
                Map<String,Object> map=(Map<String, Object>)adapterView.getItemAtPosition(i);
                String getUserId=map.get("userId").toString();
                String getId=map.get("id").toString();
                Intent intent=new Intent(MyBookActivity.this,BookDetailInfoActivity.class);
                //将车位信息Id进行传输
                intent.putExtra("userId",getUserId);
                intent.putExtra("id",getId);
                startActivity(intent);
            }
        });
    }
}
