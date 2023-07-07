package sun.project.toparking.ui.index;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sun.project.toparking.R;
import sun.project.toparking.activity.baidu.BaiduMapActivity;
import sun.project.toparking.pojo.TaskTableBean;
import sun.project.toparking.ui.adapter.MyFragmentPagerAdapter;
import sun.project.toparking.ui.base.BaseFragment;
import sun.project.toparking.ui.fragment.AllOrdersFragment;
import sun.project.toparking.ui.fragment.ChargeFragment;
import sun.project.toparking.ui.fragment.StopCarFragment;
import sun.project.toparking.util.CommonUtil;

/**
 * 数据中心
 */
public class MyDataFragment extends BaseFragment {

    ViewPager2 viewPager;

    @Override
    protected void initView() {
        initPager();
    }

    private void initPager() {

        List<String> tabs = new ArrayList<>();
        tabs.add("停车信息");
        tabs.add("待缴费");
        tabs.add("历史订单");


        TabLayout tabLayout = contentView.findViewById(R.id.home_layout);
        viewPager = contentView.findViewById(R.id.orders_pager);

        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(2)));

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabs.get(position));
            }
        });



        ArrayList<TaskTableBean> taskTableBeans = new ArrayList<>();
        taskTableBeans.add(new TaskTableBean(0));
        taskTableBeans.add(new TaskTableBean(1));
        taskTableBeans.add(new TaskTableBean(2));
        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), getLifecycle(),taskTableBeans);
        viewPager.setAdapter(pagerAdapter);
        mediator.attach();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.index_mydata;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged( hidden );
        if (hidden) {// 不在最前端界面显示
        } else {// 重新显示到最前端中
            initPager();
        }
    }
}
