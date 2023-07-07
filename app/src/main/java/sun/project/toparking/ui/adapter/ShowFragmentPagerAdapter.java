package sun.project.toparking.ui.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import sun.project.toparking.pojo.TaskTableBean;
import sun.project.toparking.sfragment.CarParkFragment;
import sun.project.toparking.sfragment.StallFragment;
import sun.project.toparking.sfragment.StallUseFragment;
import sun.project.toparking.sfragment.UserFragment;


public class ShowFragmentPagerAdapter extends FragmentStateAdapter {
    List<TaskTableBean> taskTableBeans = new ArrayList<>();

    public ShowFragmentPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<TaskTableBean> taskTableBeans) {
        super(fragmentManager, lifecycle);
        this.taskTableBeans = taskTableBeans;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(List<TaskTableBean> taskTableBeans) {
        this.taskTableBeans = taskTableBeans;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (getItemViewType(position)) {
            case 0:
                fragment = new UserFragment();
                break;
            case 1:
                fragment = new CarParkFragment();
                break;
            case 2:
                fragment = new StallFragment();
                break;
            case 3:
                fragment = new StallUseFragment();
                break;
        }

        assert fragment != null;
        return fragment;

        //return fragmentList.get(position);
    }


    @Override
    public long getItemId(int position) {
        if (taskTableBeans.size() == 0 || taskTableBeans.size() <= position) {
            return 0;
        }
        return taskTableBeans.get(position).hashCode();
        //return super.getItemId(position);
    }

    @Override
    public boolean containsItem(long itemId) {


        return super.containsItem(itemId);
    }

    @Override
    public int getItemViewType(int position) {

        TaskTableBean taskTableBean = taskTableBeans.get(position);
        return taskTableBean.getTaskType();
        //return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
