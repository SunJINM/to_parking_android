package sun.project.toparking.ui.index;

import android.content.Intent;
import android.location.Address;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.project.toparking.R;
import sun.project.toparking.activity.ShowStallActivity;
import sun.project.toparking.activity.SoundParkingActivity;
import sun.project.toparking.ui.base.BaseFragment;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.LocationUtil;


/**
 * 控制台
 */
public class ConsoleFragment extends BaseFragment implements View.OnClickListener {

    private Intent intent;
    @Override
    protected void initView() {
        LocationUtil locationUtil = new LocationUtil();
        //获取位置
        Address address = locationUtil.getALocation(requireActivity());
        //获取城市
        //String getCity = address.getLocality();
        //myCity.setText(getCity);
        //底部菜单栏移动问题
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //建立线程访问获取动态数据接口的页面，然后控制控件
        new Thread(()->{
            String url="http://121.43.101.84:9000/to_parking/carPark/getAllCarPark";
            JSONObject jsonObject = HttpGetPostUtil.doGet(url);

            assert jsonObject != null;
            getCarParkInfo(jsonObject);
        }).start();
        contentView.findViewById(R.id.search_parking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(()->{
                    //搜索，对布局进行赋值
                    EditText searchContent=(EditText) contentView.findViewById(R.id.search_content);
                    String addressFocus=searchContent.getText().toString().trim();
                    String url="http://121.43.101.84:9000/to_parking/carPark/getLikeInfo/";
                    //如果输入内容为空
                    if(TextUtils.isEmpty(addressFocus)){
                        url="http://10.0.2.2:9000/to_parking/carPark/getAllCarPark";
                    }
                    String endUrl=url+addressFocus;
                    System.out.println("访问的接口："+endUrl);
                    JSONObject jsonObject=HttpGetPostUtil.doGet(endUrl);
                    System.out.println("模糊查询到的信息为："+jsonObject);
                    assert jsonObject != null;
                    getCarParkInfo(jsonObject);
                }).start();
            }
        });

        contentView.findViewById(R.id.sound_parking).setOnClickListener(this);


    }
    @Override
    protected int getLayoutId() {
        return R.layout.index_console;
    }


    /**
     * 建立线程访问获取动态数据接口的页面，然后控制控件
     */

    public void getCarParkInfo(JSONObject jsonObject){
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
                hashMap.put("carParkId",stallJsonObject.getString("carParkId"));
                hashMap.put("carParkName",stallJsonObject.getString("carParkName"));
                hashMap.put("province",stallJsonObject.getString("province"));
                hashMap.put("city",stallJsonObject.getString("city"));
                hashMap.put("longitude",stallJsonObject.getString("longitude"));
                hashMap.put("latitude",stallJsonObject.getString("latitude"));
                String carParkId1 = stallJsonObject.getString("carParkId");
                String url1="http://121.43.101.84:9000/to_parking/stall/getAllStallByCarParkId/";
                JSONObject jsonObject1 = HttpGetPostUtil.doGet(url1+carParkId1);
                JSONArray stallArray1=jsonObject1.getJSONArray("data");
                hashMap.put("stall_number", stallArray1.length());



                hashMapList.add(hashMap);
            }

             System.out.println(hashMapList);
            //创建一个适配器，将适配器与ListView关联，为ListView创建监听事件，
            // 然后通过getItemAlPosition()方法获取选中的值，最好通过Toast.
            // makeText()方法获取的值显示出来
            getActivity().runOnUiThread(()->{
                System.out.println("创建一个适配器，将适配器与ListView关联，为ListView创建监听事件");
                SimpleAdapter adapter=new SimpleAdapter(
                        getActivity(),
                        hashMapList,
                        R.layout.index_console_list,
                        new String[]{"carParkName","address","stall_number"},
                        new int[]{
                                R.id.console_carParkName,
                                R.id.console_address,
                                R.id.stall_number
                        });
                //将适配器与ListView关联
                ListView listView=contentView.findViewById(R.id.list_my_book);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                Log.d("stall_info","遍历完成！");
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //添加动态数据点击事件
        ListView listView=contentView.findViewById(R.id.list_my_book);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {



            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String,Object> map=(Map<String, Object>)adapterView.getItemAtPosition(i);
                //跳转到详情页
                String carParkId=map.get("carParkId").toString();
                String address = map.get("address").toString();
                LatLng enLatLng = new LatLng(Double.parseDouble(map.get("latitude").toString()),Double.parseDouble(map.get("longitude").toString()));
                Log.d("TAG", "onItemClick: 工具Id跳转到详情页"+carParkId);
                intent = new Intent(getActivity(), ShowStallActivity.class);
                //将车位信息Id进行传输
                intent.putExtra("carParkId",carParkId);
                intent.putExtra("carParkName", address);
                intent.putExtra("enLatLng", enLatLng);
                startActivity(intent);
            }

        });
    }

    @Override
    public void onClick(View view) {
        intent = new Intent(getActivity(), SoundParkingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged( hidden );
        if (hidden) {// 不在最前端界面显示
        } else {// 重新显示到最前端中
            initView();
        }
    }
}


