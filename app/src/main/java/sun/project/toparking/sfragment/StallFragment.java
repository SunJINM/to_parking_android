package sun.project.toparking.sfragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sun.project.toparking.R;
import sun.project.toparking.util.HttpGetPostUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StallFragment extends Fragment {

    View rootView;
    private ListView listView;


    private TextView carParkId;
    private TextView location;
    private TextView address;
    private TextView price;
    private TextView status;

    public StallFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StallFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StallFragment newInstance(String param1, String param2) {
        StallFragment fragment = new StallFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_stall, container, false);
        }
        initView();
        //建立线程访问获取动态数据接口的页面，然后控制控件
        new Thread(()->{
            String url="http://121.43.101.84:9000/to_parking/stall/getAllStall";
            //执行url
            System.out.println(url);
            JSONObject jsonObject = HttpGetPostUtil.doGet(url);
            getStallInfo(jsonObject);
        }).start();

        return rootView;
    }
    private void initView() {
        listView = rootView.findViewById(R.id.list_stall_info);
        carParkId = rootView.findViewById(R.id.carParkId);
        location = rootView.findViewById(R.id.location);
        address = rootView.findViewById(R.id.address);
        price = rootView.findViewById(R.id.price);
        status = rootView.findViewById(R.id.status);
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

                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("carParkId",stallJsonObject.getString("carParkId"));
                hashMap.put("location",stallJsonObject.getString("category"));
                hashMap.put("price", stallJsonObject.getString("price"));
                hashMap.put("status", stallJsonObject.getString("isBook"));
                hashMap.put("address", stallJsonObject.getString("address"));
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
            getActivity().runOnUiThread(()->{
                System.out.println("创建一个适配器，将适配器与ListView关联，为ListView创建监听事件");
                SimpleAdapter adapter=new SimpleAdapter(
                        getContext(),
                        hashMapList,
                        R.layout.show_stall_list,
                        new String[]{"carParkId","location", "price", "status", "address"},
                        new int[]{
                                R.id.carParkId,
                                R.id.location,
                                R.id.price,
                                R.id.status,
                                R.id.address
                        });

                if(!adapter.isEmpty()){
                    adapter.notifyDataSetChanged();
                    //将适配器与ListView关联
                    listView.setAdapter(adapter);
                    Log.d("stall_info","遍历完成！");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}