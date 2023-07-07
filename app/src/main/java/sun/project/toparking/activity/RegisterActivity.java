package sun.project.toparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * 注册页面
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 注册界面
     */
    private EditText registerPhone;
    private EditText registerPassword;
    private Button registerBack;
    private Button userToRegister;
    private EditText confirmPasswordText;
    //昵称
    private EditText nickNameEditText;
    //性别
    private RadioGroup radioGroupGender;
    //年龄
    private EditText editTextAge;


    /**
     * 页面布局控件初始化
     */
    public void initActivityMainControl() {
        /*
        立即注册初始化
         */
        userToRegister = (Button) findViewById(R.id.user_to_Register);
        registerPhone = (EditText) findViewById(R.id.register_phone);
        registerPassword = (EditText) findViewById(R.id.register_password);
        registerBack = (Button) findViewById(R.id.register_back);
        confirmPasswordText = (EditText) findViewById(R.id.confirm_password);
        nickNameEditText=(EditText) findViewById(R.id.nick_name);
        radioGroupGender=findViewById(R.id.gender);
        editTextAge=findViewById(R.id.age);
    }

    /**
     *
     * @param savedInstanceState 保存Activity的状态的
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_register);
        System.out.println("------------------------注册功能---------------------");
        /*初始化控件 */
        initActivityMainControl();
        userToRegister.setOnClickListener(this);
        registerBack.setOnClickListener(this);
    }
    /**
     * 立即注册
     */
    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id) {
            case R.id.user_to_Register: {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获取输入的账号、密码、并去掉两端多余的空格
                        String phone = registerPhone.getText().toString();
                        String password = registerPassword.getText().toString();
                        String confirmPassword = confirmPasswordText.getText().toString();
                        String nickName=nickNameEditText.getText().toString();
                        //先判断是否选中性别
                        String checkGender=null;
                        for (int i = 0; i < radioGroupGender.getChildCount(); i++) {
                            RadioButton radioButton= (RadioButton) radioGroupGender.getChildAt(i);
                            if(radioButton.isChecked()){
                                checkGender=radioButton.getText().toString();
                                break;
                            }
                        }
                        System.out.println("性别为："+checkGender);
                        if(TextUtils.isEmpty(checkGender)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        //判断年龄是否正确
                        String age=editTextAge.getText().toString();
                        int yourAge=0;
                        if(TextUtils.isEmpty(age)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "请输入您的年龄！", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }else {
                            yourAge= Integer.parseInt(age);
                            if(yourAge<=0||yourAge>120){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, "请正确输入您的年龄！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                        }
                        //判断账号密码昵称是否已经填写
                        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)||TextUtils.isEmpty(nickName)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "内容为空，请输入账号、密码或昵称！", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        //判断账号、密码、昵称是否规范
                        if (!(CommonUtil.regularPhone(phone) && CommonUtil.regularPassword(password) && CommonUtil.regularChineseCharacter(nickName))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "手机号、密码或昵称格式错误！", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        if (!password.equals(confirmPassword)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "两次密码输入不同！", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("phone", phone);
                        hashMap.put("password", password);
                        hashMap.put("nickname",nickName);
                        //性别、年龄、地址写死先
                        hashMap.put("age",yourAge);
                        hashMap.put("gender",checkGender);
                        hashMap.put("address","河南省郑州市");
                        String url = "http://121.43.101.84:9000/to_parking/user/register";
                        JSONObject jsonObject = HttpGetPostUtil.doPost(url, hashMap);
                        int status = 0;
                        try {
                            status = jsonObject.getInt("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (status == 200) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "用户已注册或信息填写错误！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
                break;
            }
            case R.id.register_back:{
                Intent intent=new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
        }

    }
}
