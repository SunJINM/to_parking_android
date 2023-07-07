package sun.project.toparking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import sun.project.toparking.R;
import sun.project.toparking.activity.baidu.BaiduMapActivity;
import sun.project.toparking.activity.baidu.DrivingRouteActivity;
import sun.project.toparking.alipay.AliPayActivity;

public class AdminConsoleActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_console);

        findViewById(R.id.btn_addPark).setOnClickListener(this);
        findViewById(R.id.btn_addStall).setOnClickListener(this);
        findViewById(R.id.btn_managePark).setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_addPark:
                intent = new Intent(this , AddCarParkActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_addStall:
                intent=new Intent(this , AddStallActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_managePark:

                intent = new Intent(this, ShowInfoActivity.class);
                startActivity(intent);

                break;
        }
    }
}