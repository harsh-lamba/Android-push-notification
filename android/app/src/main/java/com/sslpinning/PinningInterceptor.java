package com.sslpinning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.LoginFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import javax.net.ssl.SSLPeerUnverifiedException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONObject;

public class PinningInterceptor implements Interceptor {
    private static final String TAG = "logging";
    private static String sslPinning;
    private MainActivity instance;

    public PinningInterceptor(MainActivity context) {
        instance = context;
    }

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        try{
            Request request = chain.request();
            Response response = chain.proceed(request);
            return response;
        }catch(SSLPeerUnverifiedException e){
            sslPinning = e.getMessage();
            Log.v(TAG, "SSL:pin" + e);
            Log.v(TAG, "SSL Error" + sslPinning);

            if (sslPinning.contains("Certificate pinning failure!")){
                //Cannot return null - it breaks the application
                if(instance == null){
                    throw e;
                }

                Message message = instance.mHandler.obtainMessage(0);
                message.sendToTarget();
            }
            throw e;
        }
    }

}
