package sun.project.toparking.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import sun.project.toparking.MainActivity;
import sun.project.toparking.constant.Constant;
import sun.project.toparking.pojo.User;

/**
 * SharedPreferences数据存储工具类
 */
public class SharedPreferencesUtil {
    /**
     * SharedPreferences数据存储用户的账号、密码-记住密码功能
     * @param context 上下文
     * @param user 用户信息对象
     * @ username 用户名
     * @ password 密码
     * @return boolean 的结果
     */

    public static void saveUserInfo(Context context, User user){
        SharedPreferences sp=context.getSharedPreferences(Constant.SP_LOGIN_REMEMBER,Context.MODE_PRIVATE);
        //使用其内部类对象Editor获取其编译对象
        SharedPreferences.Editor editor=sp.edit();
        //存其对象的键值
        editor.putString("phone",user.getPhone());
        editor.putString("password",user.getPassword());
        //执行事务处理
        editor.commit();
    }

    /**
     * 获取用户信息
     * @param context 调取上下文信息
     * @return
     */
    public static User getUserInfo(Context context){
        SharedPreferences sp=context.getSharedPreferences(Constant.SP_LOGIN_REMEMBER,Context.MODE_PRIVATE);
        //定义用户信息接收对象
        User user= new User();
        //当取出来的为空时，默认为第二个值
        user.setPhone(sp.getString("phone",null));
        user.setPassword(sp.getString("password",null));
        return user;
    }
    /**
     * 登录成功后，保存用户信息，退出系统时，则删出信息(保存用户的标识ID)
     * @param context 上下文
     * @param userId 用户标识
     * @param isSave 判断是存还是删 （0-删，1-存）
     */
    public static void handlerLoginInfo(Context context,String userId,Integer isSave){
        //判断是否为空，空不做任何操作
        if(userId==null){
            Log.d("handlerLoginInfo", "对象为空！不做任何操作！");
            return;
        }else {
            SharedPreferences.Editor editor=context.getSharedPreferences(Constant.SP_LOGIN_ID,context.MODE_PRIVATE).edit();
            //如果非空，则判断存、还是删
            if(isSave==1){
                Log.d("handlerLoginInfo", "保存用户标识: "+userId);
                editor.putString("userId",userId);
                editor.commit();
            }else if (isSave==0){
                Log.d("handlerLoginInfo", "删除用户标识 "+userId);
                //取值
                editor.remove("userId");
                editor.commit();
            }
        }
    }
    /**
     * 获取用户标识
     */
    public static String getUserId(Context context){
        SharedPreferences sp=context.getSharedPreferences(Constant.SP_LOGIN_ID,Context.MODE_PRIVATE);
        return sp.getString("userId",null);
    }
    /**
     * 清空登录信息
     */
    public static void removeUserId(){
        Log.d("removeUserId", "清空保存的所有内容");
        Context context=MainActivity.getContext();
        SharedPreferences.Editor editor=context.getSharedPreferences(Constant.SP_LOGIN_ID,context.MODE_PRIVATE).edit();
        //清空保存的所有内容
        editor.clear();
        editor.commit();
    }
    public static void main(String[] args) {
    }
}
