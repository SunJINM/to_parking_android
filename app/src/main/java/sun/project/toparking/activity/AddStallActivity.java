package sun.project.toparking.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sun.project.toparking.MainActivity;
import sun.project.toparking.R;
import sun.project.toparking.util.CommonUtil;
import sun.project.toparking.util.HttpGetPostUtil;
import sun.project.toparking.util.LocationUtil;

/**
 * 停车位添加管理
 */
public class AddStallActivity extends AppCompatActivity implements View.OnClickListener {
   //拍照
   private Integer TV_choose=-1;
    //拍照
    private static final int TV_TAKE_PHOTO = 1;
    //获取相册的图片
    private static final int TV_choose_PHOTO = 2;
    private static final int TV_ERROR_EXIT = -1;
    //uri对象
    private Uri TV_imageUri;
    //获取照片
    private String TV_imagePath = null;

    //之前的
    private TextView buttonGetLocation;
    private TextView getProvince;
    private TextView getCity;
    private TextView getAddress;
    private TextView getLongitude;
    private TextView getLatitude;
    private TextView getCategory;
    private TextView getPrice;
    private TextView reGet;
    //停车位信息上传按钮
    private Button buttonUpload;
    private String imagePath;
    //照片
    private ImageView getStallImage;
    //显示照片
    private Bitmap bitmapDisplayImage=null;
    //上传照片
    private ActivityResultLauncher<Intent> intentActivityResultLauncher=null;
    private TextView getCarParkId;

    /**
     * 组件初始化
     */
    public void init(){
        buttonGetLocation=(TextView) findViewById(R.id.get_location);
        getProvince=(TextView) findViewById(R.id.get_province);
        getCity=(TextView) findViewById(R.id.get_city);
        getAddress=(TextView) findViewById(R.id.get_detail_address);
        getLongitude=(TextView) findViewById(R.id.get_longitude);
        getLatitude=(TextView) findViewById(R.id.get_latitude);
        buttonUpload=(Button) findViewById(R.id.upload_stall);
        getStallImage=(ImageView) findViewById(R.id.get_stall_image);
        getCategory=(TextView) findViewById(R.id.get_stall_type);
        getPrice=(TextView) findViewById(R.id.get_stall_price);
        getCarParkId = (TextView)findViewById(R.id.get_carPark_id);
        reGet=(TextView) findViewById(R.id.re_get);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_stall);
        init();
        getStallImage.setOnClickListener(this);
        //开启相机权限
        ActivityCompat.requestPermissions(
                AddStallActivity.this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                1);
        //需要回调时访问事件的方法-registerForActivityResult必须在生命周期STARTED之前调用
        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    System.out.println("回调数据" + result);
                    //响应码
                    int resultCode = result.getResultCode();
                    System.out.println("选择码" + TV_choose);
                    //判断是否为拍摄图片
                    if (resultCode == RESULT_OK) {
                        switch (TV_choose) {
                            case TV_TAKE_PHOTO: {
                                //已拍摄
                                System.out.println("已拍摄!");
                                //上传照片
                                upload();
                                //获取照片路径

                                break;
                            }
                            case TV_choose_PHOTO: {
                                //获取相册
                                System.out.println("已获取到照片！");
                                // 判断手机系统版本号
                                int version = Build.VERSION.SDK_INT;
                                System.out.println("判断手机系统版本号" + version);
                                File outputImage;
                                try {
                                    //                // 创建File对象，用于存储拍照后的图片
                                    outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                                    System.out.println("------------------------01" + getExternalCacheDir());
                                    try {
                                        if (outputImage.exists()) {
                                            outputImage.delete();
                                        }
                                        outputImage.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Uri uri = result.getData().getData();
                                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                                    System.out.println("获取imageBitmap"+imageBitmap);
                                    FileOutputStream fileOutputStream=new FileOutputStream(outputImage);
                                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                    Log.e("saveBitMap", "saveBitmap: 图片保存到" + getExternalCacheDir() +"/output_image.jpg");
                                    System.out.println("获取到相册后的图片路径为："+TV_imagePath);
                                    upload();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
//
                                break;
                            }
                            case TV_ERROR_EXIT: {
                                break;
                            }
                        }
                        //判断是否为选择图片
                    }

                });
        //开启权限
        ActivityCompat.requestPermissions(
                AddStallActivity.this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                1);
        //获取定位事件监听
        buttonGetLocation.setOnClickListener(this);
        //监控停车位信息上传
        buttonUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        System.out.println("监听按钮事件："+view);
        int id=view.getId();
        switch (id) {
            //获取定位
            case R.id.get_location: {
                LocationUtil locationUtil = new LocationUtil();
                //获取位置
                Address address = locationUtil.getALocation(AddStallActivity.this);
                //获取省份
                String province = address.getAdminArea();
                //获取城市
                String city = address.getLocality();
                //获取地址
                String detailAddress = address.getAddressLine(0);
                //获取经度
                String longitude = String.valueOf(address.getLongitude());
                //获取纬度
                String latitude = String.valueOf(address.getLatitude());
                buttonGetLocation.setText("重新获取");
                runOnUiThread(() -> {
                    //渲染组件
                    getProvince.setText(province);
                    getCity.setText(city);
                    getAddress.setText(detailAddress);
                    getLongitude.setText(longitude);
                    getLatitude.setText(latitude);
                });
                break;
            }
            //上传定位
            case R.id.upload_stall: {
                Log.d("upload_stall", "onClick: 上传停车位详细信息！");
                new Thread(() -> {
                    //上传停车位
                    //获取省份
                    String province = getProvince.getText().toString().trim();
                    //获取城市
                    String city = getCity.getText().toString().trim();
                    //获取地址
                    String detailAddress = getAddress.getText().toString().trim();
                    //获取经度
                    String longitude = getLongitude.getText().toString().trim();
                    //获取纬度
                    String latitude = getLatitude.getText().toString().trim();
                    //获取停车场ID
                    String carParkId = getCarParkId.getText().toString().trim();
                    //获取类型
                    String category=getCategory.getText().toString().trim();

                    //获取价格
                    String price=getPrice.getText().toString().trim();
                    //以上信息均不能为空
                    if (TextUtils.isEmpty(province) || TextUtils.isEmpty(city) || TextUtils.isEmpty(detailAddress)
                            || TextUtils.isEmpty(longitude) || TextUtils.isEmpty(latitude)
                            || TextUtils.isEmpty(category)  || TextUtils.isEmpty(price)|| !CommonUtil.verifyNumberIsInt(price)
                    ) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddStallActivity.this, "请先定位车位位置!且完善车位信息", Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        //将信息保存至接口
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("province", province);
                        hashMap.put("city", city);
                        hashMap.put("address", detailAddress);
                        hashMap.put("longitude", longitude);
                        hashMap.put("latitude", latitude);
                        hashMap.put("carParkId", carParkId);
                        hashMap.put("category", category);
                        hashMap.put("price", price);
                        //获取照片
                        hashMap.put("url", imagePath);
                        String url = "http://121.43.101.84:9000/to_parking/stall/addStall";
                        HttpGetPostUtil.doPost(url, hashMap);
                        runOnUiThread(() -> {
                            Toast.makeText(AddStallActivity.this, "已上传！", Toast.LENGTH_SHORT).show();//创建一个警告对话框
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("温馨提示！");
                            builder.setMessage("停车位信息上传已完成，请点击退出！");
                            AlertDialog.Builder builder1 = builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(AddStallActivity.this, "已退出！", Toast.LENGTH_SHORT).show();//创建一个警告对话框
                                    Intent intent=new Intent(AddStallActivity.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });

                            AlertDialog alertDialog =builder.create();//这个方法可以返回一个alertDialog对象
                            alertDialog.show();
                            });
                    }
                }).start();
                break;
            }
            case R.id.get_stall_image:{
                Dialog dialog=CommonUtil.appear(AddStallActivity.this,R.layout.dialog);
                dialog.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("DialogActivity", "onCreate:  实现拍摄！");
                        dialog.dismiss();
                        System.out.println("实现拍照的照片！");
//                // 创建File对象，用于存储拍照后的图片
                        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                        System.out.println("------------------------01" + getExternalCacheDir());
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("SDK版本：" + Build.VERSION.SDK_INT);
                        if (Build.VERSION.SDK_INT < 24) {
                            TV_imageUri = Uri.fromFile(outputImage);
                        } else {
                            TV_imageUri = FileProvider.getUriForFile(AddStallActivity.this, "sun.project.toparking.fileprovider", outputImage);
                        }
//                // 启动相机程序
                        System.out.println("获取照片路径：" + TV_imageUri);
                        System.out.println("获取照片路径：" + TV_imageUri.getPath());
//                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, TV_imageUri);
//                startActivity(intent);
//                //需要回调时访问事件的方法-registerForActivityResult必须在生命周期STARTED之前调用
//                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                        result -> {
//                            Intent data=result.getData();
//                            System.out.println("回调数据"+data);
//
//                        })
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE")
                                .putExtra(MediaStore.EXTRA_OUTPUT, TV_imageUri);
                        TV_choose=TV_TAKE_PHOTO;
                        intentActivityResultLauncher.launch( intent);

                    }
                });

                dialog.findViewById(R.id.tv_take_pic).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("DialogActivity", "onCreate: 获取相册！");
                        dialog.dismiss();
                        if (ContextCompat.checkSelfPermission(AddStallActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AddStallActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                        } else {
                            //打开相册
                            System.out.println("打开相册");
                            Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            TV_choose=TV_choose_PHOTO;
                            intentActivityResultLauncher.launch(intent);
                        }
                    }
                });

                dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("DialogActivity", "onCreate:取消操作！");
                        dialog.dismiss();
                        Intent intent=new Intent(AddStallActivity.this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                break;
            }
        }
    }

    /**
     * 显示照片在布局上
     */
    public Bitmap displayImage(String imagePath){
            try {
                System.out.println(imagePath);
                URL url=new URL(imagePath);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream=connection.getInputStream();
                bitmapDisplayImage= BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return bitmapDisplayImage;
    }

    /**
     * 上传照片
     */
    public void upload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                if(TV_choose==TV_TAKE_PHOTO){
                TV_imagePath="/storage/emulated/0/Android/data/sun.project.toparking/cache/output_image.jpg";
//                }
                Log.d("DialogActivity", "upload-run: 上传照片！");
//                File file = new File("/storage/emulated/0/Android/data/sun.project.toparking/cache/output_image.jpg");
                File file=new File(TV_imagePath);
                System.out.println("测试返回结果imagePath:"+TV_imagePath);
                MediaType mediaType=MediaType.Companion.parse("text/x-markdown; charset=utf-8");
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody fileBody=RequestBody.Companion.create(file,mediaType);
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(),fileBody)
                        .build();

                String url = "http://10.0.2.2:9000/to_parking/uploadFile/uploadImage";
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                System.out.println(request);
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("上传失败！");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
//                        Gson gson = new GsonBuilder().create();
//                        System.out.println(gson);
                        try {
                            JSONObject jsonObject=new JSONObject(result);
                            String getImagePath=jsonObject.getJSONObject("data").getString("filepath");
                            System.out.println("图片上传成功"+getImagePath);
                            imagePath=getImagePath;
                            //获取照片信息

                            new Thread(()->{
                                Bitmap getImageBitmap=displayImage(imagePath);
                                Message message = new Message();
                                message.obj = getImageBitmap;
                                runOnUiThread(()->{
                                    getStallImage.setImageBitmap((Bitmap) message.obj);
                                    reGet.setText("可点击下方重新获取");
                                });
                            }).start();
                            //照片布局渲染
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}
