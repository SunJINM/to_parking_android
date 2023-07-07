package sun.project.toparking.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.SharedPreferencesUtil;

/**
 * @describe 用户个人信息页面Activity
 */
public class PersonInfoActivity extends AppCompatActivity {
    private TextView textViewNickname;
    private TextView textViewUserId;
    private TextView textViewGender;
    private TextView textViewAge;
    private TextView textViewAddress;
    private TextView textViewPhone;
    /**
     * 初始化组件
     */
    public void init(){
        textViewNickname=(TextView) findViewById(R.id.my_nickname);
        textViewUserId=(TextView) findViewById(R.id.my_id);
        textViewGender=(TextView) findViewById(R.id.my_gender);
        textViewAge=(TextView) findViewById(R.id.my_age);
        textViewAddress=(TextView) findViewById(R.id.my_address);
        textViewPhone=(TextView) findViewById(R.id.my_phone);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);
        //组件初始化
        init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取个人信息
                String getUserId= SharedPreferencesUtil.getUserId(MainActivity.getContext());
                Long userId= Long.valueOf(getUserId);
                String url="http://121.43.101.84:9000/to_parking/user/getInfoByUserId/"+userId;
                JSONObject jsonObject= HttpGetPostUtil.doGet(url);
                try {
                    String nickname=jsonObject.getJSONObject("data").getString("nickname");
                    String phone=jsonObject.getJSONObject("data").getString("phone");
//                    String password=jsonObject.getJSONObject("data").getString("password");
                    String age= String.valueOf(jsonObject.getJSONObject("data").getInt("age"));
                    String gender=jsonObject.getJSONObject("data").getString("gender");
                    String address=jsonObject.getJSONObject("data").getString("address");
                    System.out.println("已获取到所有用户信息："+nickname);
//                    //给组件赋值
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //类型问题
                            Toast.makeText(PersonInfoActivity.this, "个人信息", Toast.LENGTH_SHORT).show();
                            textViewNickname.setText(nickname);
                            textViewUserId.setText(getUserId);
                            textViewGender.setText(gender);
                            //类型问题报错
                            textViewAge.setText(age);
                            textViewAddress.setText(address);
                            textViewPhone.setText(phone);
                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
