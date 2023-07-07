package sun.project.toparking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.activity.BookDetailInfoActivity;
import sun.project.toparking.activity.CarDetailActivity;
import sun.project.toparking.activity.ChargeDetailActivity;
import sun.project.toparking.activity.MyBookActivity;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChargeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChargeFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private String mTextString;

    View rootView;
    private ListView listView;
    private TextView myStopPark;
    private TextView waitPayment;
    private TextView myStopDetail;

    private View emptyView;


    public ChargeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChargeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChargeFragment newInstance(String param1) {
        ChargeFragment fragment = new ChargeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTextString = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_charge, container, false);
        }
        initView();
        String userId= SharedPreferencesUtil.getUserId(MainActivity.getContext());
        new Thread(()->{
            String url="http://121.43.101.84:9000/to_parking/stallUse/getStallByUserId/";
            //执行url
            System.out.println(url+userId);
            JSONObject jsonObject = HttpGetPostUtil.doGet(url+userId);
            getStallInfo(jsonObject);
        }).start();
        return rootView;
    }

    private void getStallInfo(JSONObject jsonObject) {
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

                boolean isUse = stallJsonObject.getBoolean("isUse");
                boolean isPayment = stallJsonObject.getBoolean("isPayment");
                if(isPayment == false && isUse == false){
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("userId",stallJsonObject.getString("userId"));
                    hashMap.put("id",stallJsonObject.getString("id"));
                    hashMap.put("carParkName", stallJsonObject.getString("carParkName"));
                    hashMap.put("isPayment", "待缴费");
                    //获取图片链接
                    hashMapList.add(hashMap);
                }
            }
            /**
             * 映射的数据为
             */
            System.out.println(hashMapList);
            //创建一个适配器，将适配器与ListView关联，为ListView创建监听事件，
            // 然后通过getItemAlPosition()方法获取选中的值，最好通过Toast.
            // makeText()方法获取的值显示出来
           getActivity().runOnUiThread(()->{
                System.out.println("创建一个适配器，将适配器与ListView关联，为ListView创建监听事件");
                SimpleAdapter adapter=new SimpleAdapter(
                        getContext(),
                        hashMapList,
                        R.layout.charge_list,
                        new String[]{"carParkName","isPayment"},
                        new int[]{
                                R.id.my_stop_park,
                                R.id.wait_payment,
                        });
                listView.setEmptyView(emptyView);
                if(!adapter.isEmpty()){
                    //adapter.notifyDataSetChanged();
                    //将适配器与ListView关联
                    listView.setAdapter(adapter);
                    Log.d("stall_info","遍历完成！");
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!hashMapList.isEmpty()){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //获取选择项
                    Map<String,Object> map=(Map<String, Object>)adapterView.getItemAtPosition(i);
                    String getUserId=map.get("userId").toString();
                    String getId=map.get("id").toString();
                    Intent intent=new Intent(getActivity(), ChargeDetailActivity.class);
                    //将车位信息Id进行传输
                    intent.putExtra("userId",getUserId);
                    intent.putExtra("id",getId);
                    startActivity(intent);
                }
            });
        }
    }

    private void initView() {
        listView = rootView.findViewById(R.id.list_my_charge_info);
        emptyView = rootView.findViewById(R.id.emptyView);
        myStopPark = rootView.findViewById(R.id.my_stop_park);
        waitPayment = rootView.findViewById(R.id.wait_payment);
        myStopDetail = rootView.findViewById(R.id.my_stop_detail);
    }
}