package sun.project.toparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.util.CommonUtil;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.SharedPreferencesUtil;

/**
 * 充值中心
 */
public class MyRechargeActivity extends AppCompatActivity {
    private EditText editTextRechargeMoney;
    private Button buttonRecharge;

    /**
     * 组件初始化
     */
    public void init(){
        editTextRechargeMoney=(EditText) findViewById(R.id.to_my_balance);
        buttonRecharge=(Button)findViewById(R.id.to_recharge);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_recharge);
        init();
        //监听充值的面额
        buttonRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rechargeMoney=editTextRechargeMoney.getText().toString().trim();
                boolean flag= CommonUtil.verifyNumberIsInt(rechargeMoney);
                if(flag){
                    //为整数后，是否符合范围
                    int money= Integer.parseInt(rechargeMoney);
                    if(money>0&&money<=10000){
                        new Thread(()->{
                            //获取个人信息
                            String getUserId= SharedPreferencesUtil.getUserId(MainActivity.getContext());
                            Long userId= Long.valueOf(getUserId);
                            HashMap<String,Object> hashMapRecharge=new HashMap<>();
                            hashMapRecharge.put("money",money);
                            hashMapRecharge.put("userId",userId);
                            String url="http://121.43.101.84:9000/to_parking/user/rechargeBalance";
                            HttpGetPostUtil.doPost(url,hashMapRecharge);
                        }).start();
                            Toast.makeText(MyRechargeActivity.this, "充值成功！", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MyRechargeActivity.this,MyMoneyActivity.class);
                        startActivity(intent);
                    }else {
                            Toast.makeText(MyRechargeActivity.this, "充值失败，注意金额格式！", Toast.LENGTH_SHORT).show();

                    }
                }else {
                        Toast.makeText(MyRechargeActivity.this, "充值失败，注意金额格式！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
