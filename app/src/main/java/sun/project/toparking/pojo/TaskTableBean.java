package sun.project.toparking.pojo;

import android.text.TextUtils;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Date;

public class TaskTableBean {


    private int taskType;

    public TaskTableBean(int taskType) {
        this.taskType = taskType;
    }

    Date nowTime = new Date();



    public int getTaskType() {
        return taskType;
    }


    @Override
    public int hashCode() {
        int hashCode=super.hashCode();
        if (!TextUtils.isEmpty(nowTime.toString())){
            hashCode += nowTime.hashCode();
        }

        return hashCode;
    }
}
