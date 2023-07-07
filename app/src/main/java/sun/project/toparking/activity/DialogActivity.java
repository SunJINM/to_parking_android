package sun.project.toparking.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

/**
 * @describe 拍照页面添加停车位的弹框显示活动
 *
 */
public class DialogActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //开启相机权限
        ActivityCompat.requestPermissions(
                DialogActivity.this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                1);
        //需要回调时访问事件的方法-registerForActivityResult必须在生命周期STARTED之前调用
        ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
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
//        showBottomDialog();
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.dialog, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
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
                    TV_imageUri = FileProvider.getUriForFile(DialogActivity.this, "sun.project.toparking.fileprovider", outputImage);
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
                if (ContextCompat.checkSelfPermission(DialogActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DialogActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
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
                Intent intent=new Intent(DialogActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
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

                String url = "http://121.43.101.84:9000/to_parking/uploadFile/uploadImage";
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
                            //跳转信息上传页面
                            Intent intent=new Intent(DialogActivity.this,AddStallActivity.class);
                            intent.putExtra("imagePath",getImagePath);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}
