package sun.project.toparking.util;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * https 协议get,post请求
 */
public class HttpGetPostUtil {
    /**
     * get请求
     */
    public static JSONObject doGet(String url) {
        try {
            //创建OkHttpClient的客户端,url要写服务器地址
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder()
                    .url(url)
                    .get()
                    .build();//创造http请求
            //接收后端返回的内容
            System.out.println("-------------成功------");
            try(Response response=client.newCall(request).execute()) {//执行发送的指令
                //获取json格式文本
                String responseDate = response.body().string();
                JSONObject jsonObject = new JSONObject(responseDate);
                Log.d("doGet", "---------------doGet: \n" + jsonObject);
                return jsonObject;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("Get请求", "doGet: 发送失败！");
            return null;
        }
    }
    /**
     * post请求
     * @param object 数据对象
     * @return JSONArray json对象
     */
    public static JSONObject doPost(String url,Object object) {
        try {
            //转化成json字符串
            String json=new Gson().toJson(object);
            System.out.println("------------------------------"+json);
            MediaType mediaType = MediaType.Companion.parse("application/json;charset=utf-8");
            RequestBody stringBody=RequestBody.create(json,mediaType);
            //创建OkHttpClient的客户端,url要写服务器地址
            OkHttpClient client=new OkHttpClient();
            /*不支持非加密的明文流量的http网络请求，https另外，
            在 res 下新增一个 xmls 目录，然后创建一个名为：network_security_config.xmls 文件（名字自定） ，
            内容如下，大概意思就是允许开启http请求;
            android:networkSecurityConfig="@xmls/network_security_config"
            */
            Request request=new Request.Builder().url(url).post(stringBody).build();//创造http请求
            //接收后端返回的内容
            Response response=client.newCall(request).execute();//执行发送的指令
            //获取json格式文本
            String responseDate=response.body().string();
            System.out.println("json格式"+responseDate);
            //获取json类型的数组
            JSONObject jsonObject=new JSONObject(responseDate);
            Log.d("doPost", jsonObject.toString());
            Log.d("Post请求", "doPost: 发送成功！");
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
            Log.d("Post请求", "doPost: 发送失败！");
            return null;
        }
    }

    /**
     * 图片上传到服务器接口
     */
    public  static  boolean doPostImage(String url,String filepath){
        //post提交param参数
        try{
            OkHttpClient client = new OkHttpClient();
            File file = new File(filepath, "imageOut.jpeg");
            if (!file.exists()){
//                Toast.makeText(NetUtil_image.this, "文件不存在", Toast.LENGTH_SHORT).show();
                System.out.println("文件不存在,上传失败！");
            }else{
//                RequestBody requestBody2 = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                //设置文件名字
                String filename= TimeUtil.getStandardTimeToDayTwo()+ UUID.randomUUID().toString().replaceAll("-","");
                RequestBody multipartBody = new MultipartBody.Builder()
                        //一定要设置这句
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", filename, RequestBody.create(MediaType.parse("application/octet-stream"), file))
                        .build();
                final Request request = new Request.Builder()
                        .url(url)
                        .post(multipartBody)
                        .build();
                client.newCall(request).execute();//执行
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
