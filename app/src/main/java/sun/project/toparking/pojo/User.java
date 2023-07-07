package sun.project.toparking.pojo;

/**
 * 用户实体类
 */
public class User {
    //账号
    private String phone;
    //用户密码
    private String password;

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public User() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
