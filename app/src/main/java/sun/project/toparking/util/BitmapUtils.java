package sun.project.toparking.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BitmapUtils {
    /**
     * 将url图片转换成bitmap文件
     *
     */
    /**
     * 显示照片在布局上
     */
    public static Bitmap displayImage(String imagePath){
         Bitmap bitmapDisplayImage=null;
        try {
            URL url=new URL(imagePath);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
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
}
