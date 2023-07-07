package sun.project.toparking.ui.index;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.activity.MyBookActivity;
import sun.project.toparking.activity.MyMoneyActivity;
import sun.project.toparking.activity.PersonInfoActivity;
import sun.project.toparking.ui.base.BaseFragment;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.SharedPreferencesUtil;

/**
 * 个人中心
 */
public class PersonFragment extends BaseFragment {
    private boolean flag;
    @Override
    protected void initView() {
        flag=false;
        //获取用户信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                String getUserId= SharedPreferencesUtil.getUserId(MainActivity.getContext());
                Long userId= Long.valueOf(getUserId);
                String url="http://121.43.101.84:9000/to_parking/user/getInfoByUserId/"+userId;
                JSONObject jsonObject= HttpGetPostUtil.doGet(url);
                String url1="http://121.43.101.84:9000/to_parking/stallUse/getUserBookNumber/"+userId;
                JSONObject jsonObject1= HttpGetPostUtil.doGet(url1);
                System.out.println("用户信息为：\n"+jsonObject);
                try {
                    Long getDataUserId=jsonObject.getJSONObject("data").getLong("userId");
                    String thisUserId=getDataUserId.toString();
                    String nickName=jsonObject.getJSONObject("data").getString("nickname");
                    int bookNumber=jsonObject1.getInt("data");
                    String getBookNumber="预约 "+bookNumber;
                    System.out.println("获取其中的值thisUserId:"+thisUserId);
                    System.out.println("获取其中的值nickName:"+nickName);
                    //操作组件
                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            TextView textViewNickname=contentView.findViewById(R.id.index_person_nickname);
                            textViewNickname.setText(nickName);
                            //TextView bookNumberTV=contentView.findViewById(R.id.book_number);
                            //bookNumberTV.setText(getBookNumber);
                            Toast.makeText(getActivity(), "欢迎使用！", Toast.LENGTH_SHORT).show();
                            flag=true;
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //监听各个组件
                //通过循环等待
                //监听退出按钮
                contentView.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //删除保留的用户信息
                        SharedPreferencesUtil.removeUserId();
                        Intent intent = new Intent(getActivity(), MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                //监听切换账号按钮
                contentView.findViewById(R.id.switch_account).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                //删除保留的用户信息
                        SharedPreferencesUtil.removeUserId();
                        Intent intent = new Intent(getActivity(), MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                /*//监听我的预约按钮
                contentView.findViewById(R.id.my_book_listen).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(), MyBookActivity.class);
                        startActivity(intent);
                    }
                });*/
            }
        }).start();
        //监听钱包按钮
                contentView.findViewById(R.id.my_info).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(), PersonInfoActivity.class);
                        startActivity(intent);
                    }
                });
        //监听钱包按钮
        contentView.findViewById(R.id.my_money01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), MyMoneyActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.index_person;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged( hidden );
        if (hidden) {// 不在最前端界面显示
        } else {// 重新显示到最前端中
            initView();
        }
    }

}
