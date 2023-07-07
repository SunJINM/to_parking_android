package sun.project.toparking.constant;

/**
 * 常量表
 */
public interface Constant {
    //SharedPreferences数据存储文件名
    String SP_LOGIN_REMEMBER="login_remember";
    /**
     * @describe 登录后保存用户的存储信息
     */
    String SP_LOGIN_ID="login_info";
    /**
     * @describe 登录后判断是保存用户的存储信息(1)，还是删除(0)
     */
    Integer SP_SAVE_ID=1;
    Integer SP_DELETE_ID=0;

}
