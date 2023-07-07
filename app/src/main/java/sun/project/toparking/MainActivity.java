package sun.project.toparking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.search.route.RoutePlanSearch;

import org.json.JSONException;
import org.json.JSONObject;

import sun.project.toparking.activity.AddStallActivity;
import sun.project.toparking.activity.AdminConsoleActivity;
import sun.project.toparking.activity.ForgetPasswordActivity;
import sun.project.toparking.activity.IndexActivity;
import sun.project.toparking.activity.RegisterActivity;
import sun.project.toparking.pojo.User;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.SharedPreferencesUtil;
import sun.project.toparking.R;

/**
 * 添加一个页面都要配置
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 登录界面控件
     */
    //登录按钮
    private Button loginButton;
    //定义账号控件
    private EditText editTextPhone;
    //定义密码控件
    private EditText editTextPassword;
    //定义记住账号和密码的控件
    private CheckBox checkBoxToRememberInfo;
    //立即注册按钮
    private TextView toRegister;
    //忘记密码
    private TextView forgetPassword;
    //把上下文暴露出来
    private static MainActivity instance;
    //添加停车位按钮
    private Button buttonAddButton;
    /**
     * 页面布局activity_main.xml的控件初始化
     */
    /**
     * 控件初始化
     */
    public void initActivityMainControl() {
        //登录
        loginButton=(Button)findViewById(R.id.login_button);
        //定义账号控件
        editTextPhone = (EditText) findViewById(R.id.phone);
        //定义密码控件
        editTextPassword = (EditText) findViewById(R.id.password);
        //定义记住账号和密码的复选框控件
        checkBoxToRememberInfo = (CheckBox) findViewById(R.id.remember_info);
        //立即注册按钮
        toRegister = (TextView) findViewById(R.id.toRegister);
        forgetPassword=(TextView) findViewById(R.id.to_forget_password);
        buttonAddButton=(Button) findViewById(R.id.add_stall);

    }

    /**
     * activity_main.xml主界面逻辑代码
     *
     * @param savedInstanceState 保存Activity的状态的
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取上下文
        instance=this;
        //如果登录得到用户未注销，重新进去就会一直在系统的首页
        String userId= SharedPreferencesUtil.getUserId(this);
        if (!(userId==null)){
            System.out.println("用户未退出系统，信息保留，直接进入主页！");
            Intent intent=new Intent(MainActivity.this, IndexActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        System.out.println("------------------------登录页面---------------------");
        /*初始化控件 */
        initActivityMainControl();
        /*
        登录页面
         */
        loginButton.setOnClickListener(this);
        //监控注册按钮
        toRegister.setOnClickListener(this);
        //监控复选框事件
        checkBoxToRememberInfo.setOnClickListener(this);
        //监听添加停车位按钮
        buttonAddButton.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        User user= SharedPreferencesUtil.getUserInfo(this);
        if((!TextUtils.isEmpty(user.getPhone()))&&(!TextUtils.isEmpty(user.getPassword()))){
            editTextPhone.setText(user.getPhone());
            editTextPassword.setText(user.getPassword());
            checkBoxToRememberInfo.setChecked(true);
        }
    }

    /**
     * 填写账号、密码时，判断是否勾选记住账号和密码内部类
     */
    @Override
    public void onClick(View view) {
        //监控Id
        int id = view.getId();
        switch (id) {
            //登录界面
            case R.id.login_button: {
                //建一个子线程进行接口请求,获取接口用户的信息（RequestBody类型-post请求）
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获取账号
                        String phone = editTextPhone.getText().toString();
                        String password = editTextPassword.getText().toString();
                        User user = new User();
                        user.setPhone(phone);
                        user.setPassword(password);
                        //获取请求的url
                        String url = "http://121.43.101.84:9000/to_parking/user/login";
                        JSONObject jsonObject = HttpGetPostUtil.doPost(url,user);
                        System.out.println("---------------------------/n----\n" + jsonObject);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean verify = false;
                                try {
                                    assert jsonObject != null;
                                    verify = jsonObject.getJSONObject("data").getBoolean("verify");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (verify == true) {
                                    System.out.println("登录成功！");
                                    //保存用户信息,根据用户手机号获取其用户标识
                                    String userId;
                                    try {
                                        Long getUserId=jsonObject.getJSONObject("data").getLong("userId");
                                        userId=getUserId.toString();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        userId=null;
                                    }
                                    SharedPreferencesUtil.handlerLoginInfo(MainActivity.this,userId,1);
                                    //页面跳转
                                    Intent intent=new Intent(MainActivity.this, IndexActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "账号不存在或账号密码错误！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }).start();
                break;
            }
            //记住密码
            case R.id.remember_info: {
                //获取输入的账号、密码
                String phone = editTextPhone.getText().toString();
                String password = editTextPassword.getText().toString();
                //判断账号密码是否已经填写
                if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "内容为空，请输入账号和密码！", Toast.LENGTH_SHORT).show();
                    checkBoxToRememberInfo.setChecked(false);
                    return;
                }
                //判断是否勾选了记住密码及账号
                boolean isChecked = checkBoxToRememberInfo.isChecked();
                if (isChecked) {
                    //实例化一个用户对象
                    User user = new User();
                    //获取输入的账号、密码、并去掉两端多余的空格
                    user.setPhone(phone);
                    user.setPassword(password);
                    SharedPreferencesUtil.saveUserInfo(this, user);
                    Toast.makeText(this, "已勾选，信息已保存！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "取消勾选，未执行操作！", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            //注册
            case R.id.toRegister:{
                //跳转到注册页面 - Intent用于启动组件和传递数据
                Intent intent = new Intent(this, RegisterActivity.class);
                //开启
                startActivity(intent);
                break;
            }
            case R.id.to_forget_password:{
                //修改密码
                //跳转到注册页面 - Intent用于启动组件和传递数据
                Intent intent = new Intent(this, ForgetPasswordActivity.class);
                //开启
                startActivity(intent);
                break;
            }
            //添加停车位
            case R.id.add_stall:{
                Intent intent=new Intent(this , AdminConsoleActivity.class);
                startActivity(intent);
            }
        }
    }
    /**
     * 获取上下文
     *
     */
    public static MainActivity getContext(){
        return instance;
    }

}
