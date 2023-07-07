package sun.project.toparking.activity;

import static com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_LABELED;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import sun.project.toparking.R;
import sun.project.toparking.ui.index.ConsoleFragment;
import sun.project.toparking.ui.index.MyDataFragment;
import sun.project.toparking.ui.index.PersonFragment;

/**
 * 应用首页
 */
public class IndexActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {


    private BottomNavigationView index_view;
    //fragments数组
    private Fragment[] fragments;
    //全局
    private int indexFragment=0;
    private Button personUpdatePassword;
    /**
     * 控件初始化
     */
    public void initActivityMainControl() {
        index_view=(BottomNavigationView) findViewById(R.id.index_view);
        fragments=new Fragment[]{
                new ConsoleFragment(),
                new MyDataFragment(),
                new PersonFragment()
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        //控件初始化
        initActivityMainControl();
//        index_view.setLabelVisibilityMode(LABEL_VISIBILITY_LABELED);
        index_view.setLabelVisibilityMode(LABEL_VISIBILITY_LABELED);
        index_view.setOnItemSelectedListener(this);
        //配置默认的首页布局事务开启
        //查看请求的默认值
        Intent intent=getIntent();
        int index=intent.getIntExtra("index",-1);
        if(index!=-1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.index_frame, fragments[index])
                    .commit();
        }else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.index_frame, fragments[indexFragment])
                    .commit();
        }
    }
    /**
     * 完成切换fragment
     */
    private void switchFragment(int to){
        if(indexFragment==to){
            return;
        }
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        //把之前的隐藏-提交
        fragmentTransaction.hide(fragments[indexFragment])
                .commitAllowingStateLoss();
        //判断是否被添加过
        if(!fragments[to].isAdded()){
            fragmentTransaction.add(R.id.index_frame,fragments[to]);
        }else {
            fragmentTransaction.show(fragments[to]);
        }
        indexFragment=to;
    }
    /**
     * 监控控件事件
     * @param item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.console:{
                System.out.println("---------------------首页-----------------");
                switchFragment(0);
                return true;
            }
            case R.id.data_center:{
                System.out.println("----------------------数据中心-----------------------");
                switchFragment(1);
                return true;
            }
            case R.id.personal_center:{
                System.out.println("-----------------我的----------------------");
                switchFragment(2);
                return true;
            }
        }
        return false;
    }
}
