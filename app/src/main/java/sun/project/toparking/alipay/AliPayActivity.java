package sun.project.toparking.alipay;

import java.util.Map;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import sun.project.toparking.R;
import sun.project.toparking.alipay.AuthResult;
import sun.project.toparking.alipay.PayResult;
import sun.project.toparking.util.HttpGetPostUtil;

/**
 *  重要说明：
 *
 *  本 Demo 只是为了方便直接向商户展示支付宝的整个支付流程，所以将加签过程直接放在客户端完成
 *  （包括 OrderInfoUtil2_0_HK 和 OrderInfoUtil2_0）。
 *
 *  在真实 App 中，私钥（如 RSA_PRIVATE 等）数据严禁放在客户端，同时加签过程务必要放在服务端完成，
 *  否则可能造成商户私密数据泄露或被盗用，造成不必要的资金损失，面临各种安全风险。
 *
 *  Warning:
 *
 *  For demonstration purpose, the assembling and signing of the request parameters are done on
 *  the client side in this demo application.
 *
 *  However, in practice, both assembling and signing must be carried out on the server side.
 */
public class AliPayActivity extends AppCompatActivity {

    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2021000122689454";

    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    public static final String PID = "2088721013427885";

    /**
     * 用于支付宝账户登录授权业务的入参 target_id。
     */
    public static final String TARGET_ID = "wltvfj3911@sandbox.com";

    /**
     *  pkcs8 格式的商户私钥。
     *
     * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * 	RSA2_PRIVATE。
     *
     * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCnfDhJzAw+Wk4VSYpoAJYd7LOJQQIjX+JkvyTl8CCBo70KdH9ItK3cOD49XJqoTy0SHJjONW5cLJDR4uYFNPRgAw1W6ImynREmDgytsRcWOsnY18PgRA1hz4EhX1XLb2Kph/oIwT3mepKbEWtTjJMVdu+wmAe5mL7nX4+9HOEIg/pG+Bs3TW/EC5uyGaOcMAiQEwGUjlzMSyPNm23X/TsrM878uA61dXs79XD1oNCwi+x/VTRGNyLVDcQFTEh5sjvopG1NCJAcb2G2Gj9L1SxD9n+d9czGwktC0ol15yBTCNJaeKqWEq21LbrbDxu4nr1PVZyST1eEcZnm2jO12avzAgMBAAECggEAHTZ+fui3TfzkV7QvDvrjNC8AFbWgENl9nIL2haC/mr0bkQJG3UCefqXUvVE0rJy6yMjyIi2RpUQksmc4JbpZ0ZFWt0zyD5Rz1EZpEbasxzTT1h20TZ6xEtlltZNyXHx+IgHUq8hA6r2Aasm8BaOkV/8ZyZyK5GVvDt5NLPgnjBtomh/SQJQSYrQl63SvjgZdchkYlWyzEkFG+UA4ZZUI1CocXD1HCz0agwaKdApEeC4bSRUBEWmPRILLpioDglMrUwW5O62Zb0EbTAR1EeiGKJUgxG2MXMmgzLP13dfjO2IE1yQ6zQRPO6TEXuDRcq08o5SqWPKzDFsa7WdAEXQ+OQKBgQDdrMGYewSdv2GiKqjxrFyhL9s8MZ6+YCKXWZnya8Tq/8M290YtpNvQ09kLKHjRhIeQzMmZJ0LzbJDXhD9AWdMW9tfRu7YIsTVFvdpkLUetyTDJxD0XBRIx+Es5ay9jJXsD20c/86ptL5ZZWAyrDbzswArkKNePdr4cKdXhT/y+jwKBgQDBa2AAKYsn6LPnfMQ6pNNwJPhmTphEK7J0E0bCLHk0kwTn3/acAj3Zysh+jrl8u2aeOr8oC3pzqtI/AlLjyrWy1iPalDFWMohS0Bmriy3AvqVXZbUE+8JLAEsw6rhueZpA3aHXiXTa3vLGi20IvhT1VjjUAva9e1KJK8HjE+luXQKBgB4fvedEwGnXeiMZfP/qlmPpJvfVHmdslQbQTFVfQ9FTlGnBzK2jO6PzRgl4JIMEaY/J5JOFfFmsJTrEY/wQuNjkDowX4DLqMf0aQbVfWeBJ+PFSU6LvM3vK7B4PbM8iZsctY09Tg5N25g2aI46TPVtG9bPvwOtkDY2yObh2sDgNAoGAfLjUisY/KVjwyVC26GnovePZGX+MyEi1JgxYEFXFQpWbEDpnN2pqn8oXRySZj0j4fwe6xAPCzltUf7Tr5l+Mkulq7UOqHL8xENWH+AojUqqcy6KV+8SsINjro3t3uiVHxpQkDGxiodAqFMVLZdvMf/c9ZdkTalPfgPxqZhcs6zECgYA3/GSaSpjFb9iH6J1ASdil0+HW7ieQVPhE12g6CyaJwGby9Gu2PXlIlN64MteRPRy5G8w9JiRNuLJXRTl0a8JVHlUVCtJX9Q8ZR4oTF1mqYJmML+s38RfADfRMDUdVnwpbdbabw0ivDOwEgV/On3zXv3yZiJbftIJAUKFE4ILB2w==";


    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {

                        new Thread(()->{
                            String url = "http://121.43.101.84:9000/to_parking/stallUse/successPayment/";
                            System.out.println(url+id);
                            JSONObject jsonObject = HttpGetPostUtil.doGet(url+id);

                        }).start();

                        finish();
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showAlert(AliPayActivity.this, getString(R.string.pay_success));
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showAlert(AliPayActivity.this, getString(R.string.pay_failed) + payResult);
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        showAlert(AliPayActivity.this, getString(R.string.auth_success) + authResult);
                    } else {
                        // 其他状态值则为授权失败
                        showAlert(AliPayActivity.this, getString(R.string.auth_failed) + authResult);
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };
    private String price;
    private String id;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_pay);

        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);

        intent = AliPayActivity.this.getIntent();

        price = String.valueOf(intent.getIntExtra("price", 0));

        if(price.equals("0")){
            price = "0.01";
        }
        id = intent.getStringExtra("id");
    }

    /**
     * 支付宝支付业务示例
     */
    public void payV2(View v) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            showAlert(this, getString(R.string.error_missing_appid_rsa_private));
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */



        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2, price);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(AliPayActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 支付宝账户授权业务示例
     */
    public void authV2(View v) {
        if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
                || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
                || TextUtils.isEmpty(TARGET_ID)) {
            showAlert(this, getString(R.string.error_auth_missing_partner_appid_rsa_private_target_id));
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * authInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(AliPayActivity.this);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);

                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }


    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }

    private static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    private static String bundleToString(Bundle bundle) {
        if (bundle == null) {
            return "null";
        }
        final StringBuilder sb = new StringBuilder();
        for (String key: bundle.keySet()) {
            sb.append(key).append("=>").append(bundle.get(key)).append("\n");
        }
        return sb.toString();
    }
}
