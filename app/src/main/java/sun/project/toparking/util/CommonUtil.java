package sun.project.toparking.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import sun.project.toparking.R;

/**
 * 常用的工具类
 */
public class CommonUtil {
    /**
     * QQ 邮箱正则表达式
     * @param QQ QQ邮箱
     * @return boolean
     */
    public static boolean regularQQ(String QQ){
        String regex="^[a-z_\\d]+(?:\\.[a-z_\\d]+)*@qq\\.com$";
        boolean flag=QQ.matches(regex);
        if(flag){
            return true;
        }else {
            return false;
        }
    }
    /**
     * 11位手机号码正则表达式
     * @param phone 手机号码
     * @return boolean
     */
    public static boolean regularPhone(String phone){
        String regex="^[1]([0-9]{10})*$";
        boolean flag=phone.matches(regex);
        System.out.println(phone);
        if(flag){
            return true;
        }else {
            return false;
        }
    }
    /**
     * 6-16密码
     * @return
     */
    public static boolean regularPassword(String password){
        String regex="^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        boolean flag=password.matches(regex);
        if(flag){
            return true;
        }else {
            return false;
        }
    }
    /**
     * 20位中文字符正则表达式:^[\u4e00-\u9fa5\w]{1,20}$
     * @return
     */
    public static boolean regularChineseCharacter (String character){
        String regex="^[\\u4e00-\\u9fa5\\w]{1,20}$";
        boolean flag=character.matches(regex);
        if(flag){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 整数的正则表达式
     */
    public static boolean verifyNumberIsInt(String number){
        String regex="^[0-9]*[1-9][0-9]*$";
        boolean flag=number.matches(regex);
        if(flag){
            return true;
        }else {
            return false;
        }
    }
    public static void main(String[] args) {
        //中文正则表达式:^[\u4e00-\u9fa5\w]{1,20}$
//        System.out.println(regularPhone("18813447134"));
//        System.out.println(regularPassword("188134mm"));
        System.out.println(verifyNumberIsInt("0"));
        System.out.println(verifyNumberIsInt("0.1"));
        System.out.println(verifyNumberIsInt("-0.1"));
        System.out.println(verifyNumberIsInt("10"));
        System.out.println(verifyNumberIsInt("10000000"));
        System.out.println(verifyNumberIsInt("10.00"));
        System.out.println(verifyNumberIsInt("10.22"));
        System.out.println(verifyNumberIsInt("10.02"));
        System.out.println(verifyNumberIsInt("-10.02"));
        System.out.println(verifyNumberIsInt("10.02"));
    }

    public static Dialog appear(Context context, int resource){
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        //2、设置布局
        View view1 = View.inflate(context,resource, null);
        dialog.setContentView(view1);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        return dialog;
    }
}
