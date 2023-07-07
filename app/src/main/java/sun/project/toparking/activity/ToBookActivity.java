package sun.project.toparking.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import sun.project.toparking.R;
import sun.project.toparking.util.HttpGetPostUtil;

public class ToBookActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_region_number;
    private TextView tv_random_number;
    private Spinner sp_region;
    private Spinner sp_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_book);
        tv_region_number = findViewById(R.id.tv_region_number);
        tv_random_number = findViewById(R.id.tv_random_number);

        findViewById(R.id.btn_book).setOnClickListener(this);
        findViewById(R.id.btn_book1).setOnClickListener(this);
        sp_region = findViewById(R.id.sp_region);
        sp_number = findViewById(R.id.sp_number);

    }

    private void initSpinnerForDropdown() {
        // 声明一个下拉列表的数组适配器
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, regionArray);
        ArrayAdapter<String> numberAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, numberArray);
        // 从布局文件中获取名叫sp_dropdown的下拉框
        // 设置下拉框的标题。对话框模式才显示标题，下拉模式不显示标题
        sp_region.setAdapter(regionAdapter); // 设置下拉框的数组适配器
        sp_number.setAdapter(numberAdapter);
        sp_region.setSelection(0); // 设置下拉框默认显示第一项
        sp_number.setSelection(0);
        // 给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp_region.setOnItemSelectedListener(new MyRegionListener());
        sp_number.setOnItemSelectedListener(new MyNumberListener());
    }

    // 定义下拉列表需要显示的文本数组
    private String[] regionArray = {"A", "B", "C", "D"};
    private String[] numberArray = {"1", "2"};

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    class MyRegionListener implements AdapterView.OnItemSelectedListener {
        // 选择事件的处理方法，其中arg2代表选择项的序号
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Toast.makeText(ToBookActivity.this, "您选择的是" + regionArray[arg2],
                    Toast.LENGTH_LONG).show();
        }

        // 未选择时的处理方法，通常无需关注
        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    class MyNumberListener implements AdapterView.OnItemSelectedListener {
        // 选择事件的处理方法，其中arg2代表选择项的序号
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Toast.makeText(ToBookActivity.this, "您选择的是" + numberArray[arg2],
                    Toast.LENGTH_LONG).show();
        }

        // 未选择时的处理方法，通常无需关注
        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_book:


                break;
            case R.id.btn_book1:

                //建立线程访问url
                new Thread(()->{
                    Intent intent = ToBookActivity.this.getIntent();
                    String getUserId = intent.getStringExtra("getUserId");
                    String getStallId = intent.getStringExtra("getStallId");
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(ToBookActivity.this);
                            builder.setTitle("温馨提示！");
                            builder.setMessage("您已预约成功，请点击确定返回！");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(ToBookActivity.this, "已返回！", Toast.LENGTH_SHORT).show();//创建一个警告对话框
                                    //预约成功要刷新页面
                                }
                            }).setIcon(R.drawable.re_get);
                            AlertDialog alertDialog =builder.create();//这个方法可以返回一个alertDialog对象
                            alertDialog.show();
                            tv_random_number.setText("区域B号码1");
                        });
                    }
                    if(status!=200 && verify==false){
                        //预约失败，你有预约车位流程还在进行中
                        runOnUiThread(()->{
                            //预约成功
                            AlertDialog.Builder builder = new AlertDialog.Builder(ToBookActivity.this);
                            builder.setTitle("温馨提示！");
                            builder.setMessage("预约失败！您有预约还在进行中！请点击退出！");
                            builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(ToBookActivity.this, "已返回！", Toast.LENGTH_SHORT).show();//创建一个警告对话框
                                }
                            }).setIcon(R.drawable.re_get);
                            AlertDialog alertDialog =builder.create();//这个方法可以返回一个alertDialog对象
                            alertDialog.show();
                        });
                    }
                }).start();
                break;
        }
    }
}