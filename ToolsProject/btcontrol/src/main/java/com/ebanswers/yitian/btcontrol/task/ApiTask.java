package com.ebanswers.yitian.btcontrol.task;

import android.text.TextUtils;
import android.util.Log;

import com.ebanswers.sdk.util.NetWorkUtils;
import com.ebanswers.yitian.btcontrol.CoreApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Callanna on 2017/3/17.
 */

public class ApiTask {
    public static final String FACTROY_ID = "ecc8b0ac-579a-11e5-9e88-00a0d1eb6068";
    private static final String APP_ID = "530wUs2hXcvJJCoIfg";
    private static final String APP_SECRET = "1NH6443go2VVKXLl9lBraqeZRX1AB3eT";
    private static final String GET_TOKEN = "https://api.53iq.com/1/token";// 获取Token
    private static final String GET_CODE = "https://api.53iq.com/1/telephone/code";//获取手机验证码接口
    private static final String VERIFY_CODE = "https://api.53iq.com/1/telephone/code";//验证短信验证码是否正确接口
    private static final String BIND_DEVICE = "https://api.53iq.com/3/device"; //设备注册
    private static ApiTask instance;


    private static String token = "";

    private ApiTask() {

    }

    public static ApiTask getInstance() {
        synchronized (ApiTask.class) {
            if (instance == null) {
                instance = new ApiTask();
            }
            return instance;
        }
    }

    public void getToken() {
        Request request = new Request.Builder()
                .url(GET_TOKEN + "?appid=" + APP_ID + "&secret=" + APP_SECRET+"&grant_type=client_credential")
                .build();
        OkHttpUtil.enqueue(request,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                tryAgain();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.d("duanyl", "getToken: " + result);
                        JSONObject jsonResult = new JSONObject(result);
                        if (jsonResult.getInt("code") == 0) {
                            String access_token = jsonResult.getJSONObject("data").getString("access_token");
                            if (!TextUtils.isEmpty(access_token)) {
                                token = access_token;
                            }
                        } else {
                            tryAgain();
                        }
                    } else {
                        tryAgain();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tryAgain();
                }
            }
        });

    }

    private void tryAgain() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
           e1.printStackTrace();
        }
        NetWorkUtils.checkNetWorkAvailable(new NetWorkUtils.CheckNetCallBack() {
            @Override
            public void checkNetWork(boolean result) {
                if(!result){
                    getToken();
                }
            }
        });

    }

    public void getSecCode(String phone, Callback callback) {
       Request request = new Request.Builder()
                .url(GET_CODE+"?access_token="+token+"&phone="+phone)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    public void bindDevice(String phone, String code, final String device, final Callback callback) {

        verify(phone, code, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bind(device, CoreApplication.getInstance().connectMac,FACTROY_ID, callback);
            }
        });
    }

    public void verify(String phone, String code, Callback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("access_token",token)
                .add("phone", phone)
                .add("code", code)
                .build();
        final Request request = new Request.Builder()
                .url(VERIFY_CODE)
                .post(requestBody)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    public void bind(String device, String mac, String factory, Callback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("access_token",token)
                .add("mac", mac)
                .add("alias", device)
                .add("factory_id",factory)
                .build();
        final Request request = new Request.Builder()
                .url(BIND_DEVICE)
                .post(requestBody)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

}
