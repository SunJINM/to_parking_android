package sun.project.toparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.util.CommonUtil;
import sun.project.toparking.util.HttpGetPostUtil;

/**
 * 忘记密码页面
 */
public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText phoneText;
    private EditText newPasswordText;
    private Button confirmButton;
    private Button backLoginButton;
    /**
     * 的控件初始化
     */
    public void initActivityMainControl() {
        newPasswordText=(EditText) findViewById(R.id.forget_password);
        phoneText=(EditText) findViewById(R.id.forget_phone);
        confirmButton=(Button) findViewById(R.id.confirm_modify);
        backLoginButton=(Button) findViewById(R.id.forget_back);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        System.out.println("------------------------修改功能---------------------");
        /*初始化控件 */
        initActivityMainControl();
        confirmButton.setOnClickListener(this);
        backLoginButton.setOnClickListener(this);
    }
    /**
     * 立即注册
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.confirm_modify:{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获取输入的账号、密码、并去掉两端多余的空格
                        String phone = phoneText.getText().toString();
                        String password = newPasswordText.getText().toString();
                        //判断账号密码是否已经填写
                        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ForgetPasswordActivity.this, "内容为空，请输入账号和密码！", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                            //判断密码是否输入正确
                            if (!CommonUtil.regularPassword(password)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgetPasswordActivity.this, "密码格式错误，请按正确格式输入！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("phone", phone);
                            hashMap.put("password", password);
                            //接口验证账号密码
                            String url1 = "http://121.43.101.84:9000/to_parking/user/modifyPassword";
                            JSONObject jsonObject1 = HttpGetPostUtil.doPost(url1, hashMap);
                            boolean verify1 = false;
                            try {
                                verify1 = jsonObject1.getJSONObject("data").getBoolean("verify");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (verify1 == true) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgetPasswordActivity.this, "密码修改成功！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgetPasswordActivity.this, "密码修改失败，请检查输入信息！可能账号还未注册！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                    }
                }).start();
                break;


            }
            case R.id.forget_back:{
                Intent intent=new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                break;
            }
        }
    }
}
