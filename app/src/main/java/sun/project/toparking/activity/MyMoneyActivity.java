package sun.project.toparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.SharedPreferencesUtil;

/**
 * @describe 我的余额
 */
public class MyMoneyActivity extends AppCompatActivity {
    private TextView textViewMyBalance;
    private Button buttonToRecharge;

    /**
     * 初始化组件
     */
    public void init() {
        textViewMyBalance = (TextView) findViewById(R.id.my_balance);
        buttonToRecharge=(Button) findViewById(R.id.my_to_recharge);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_money);
        init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取个人信息
                String getUserId = SharedPreferencesUtil.getUserId(MainActivity.getContext());
                Long userId = Long.valueOf(getUserId);
                String url = "http://121.43.101.84:9000/to_parking/user/getInfoByUserId/" + userId;
                JSONObject jsonObject = HttpGetPostUtil.doGet(url);
                try {
                    double balance = jsonObject.getJSONObject("data").getDouble("balance");
//                    两位小数
                    DecimalFormat decimalFormat=new DecimalFormat("0.00");
                    String myBalance=decimalFormat.format(balance);
                    runOnUiThread(()->{
                        textViewMyBalance.setText(myBalance);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //监听充值的按钮
        buttonToRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MyMoneyActivity.this,MyRechargeActivity.class);
                startActivity(intent);
            }
        });

    }
}

