package sun.project.toparking.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import sun.project.toparking.R;
import sun.project.toparking.pojo.TaskTableBean;
import sun.project.toparking.ui.adapter.MyFragmentPagerAdapter;
import sun.project.toparking.ui.adapter.ShowFragmentPagerAdapter;

public class ShowInfoActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        TabLayout tabLayout = findViewById(R.id.home_layout);
        viewPager = findViewById(R.id.show_pager);

        List<String> tabs = new ArrayList<>();
        tabs.add("用户信息");
        tabs.add("停车场信息");
        tabs.add("车位信息");
        tabs.add("使用记录");

        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(3)));

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
        taskTableBeans.add(new TaskTableBean(3));
        ShowFragmentPagerAdapter pagerAdapter = new ShowFragmentPagerAdapter(getSupportFragmentManager(), getLifecycle(),taskTableBeans);
        viewPager.setAdapter(pagerAdapter);
        mediator.attach();
    }
}