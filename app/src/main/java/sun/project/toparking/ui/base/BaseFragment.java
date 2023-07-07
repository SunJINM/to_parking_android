package sun.project.toparking.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import sun.project.toparking.activity.ForgetPasswordActivity;
import sun.project.toparking.ui.index.ConsoleFragment;
import sun.project.toparking.ui.index.MyDataFragment;
import sun.project.toparking.ui.index.PersonFragment;

public abstract class BaseFragment extends Fragment{
   /*
   protected 属性在所属类中可用,或者对继承层次结构中该类下面的类可用
    */
    protected View contentView;
    protected abstract void initView();
    protected abstract int getLayoutId();
    PersonFragment personFragment;
    MyDataFragment myDataFragment;
    ConsoleFragment consoleFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personFragment=new PersonFragment();
        myDataFragment=new MyDataFragment();
        consoleFragment=new ConsoleFragment();
    }

    /**
     * 控件初始化
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
        nflate的作用就是将一个用xml定义的布局文件查找出来，注意与findViewById()的区别，inflate是加载一个布局文件，而findViewById则是从布局文件中查找一个控件
         */
        contentView=inflater.inflate(getLayoutId(),container,false);
        initView();
        return contentView;
    }
/*
<T extends View>是声明这是一个泛型方法，同时extends View限制了返回的T类型必须是View的子类
 */
    protected <T extends View> T find(@IdRes int id){
        return contentView.findViewById(id);
    }
    class UpdatePasswordClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ForgetPasswordActivity.class);//想调到哪个界面就把login改成界面对应的activity名
            startActivity(intent);
        }
    }

}
