package com.example.tiger.materialtabs.utils;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.tiger.materialtabs.receiver.ConnectivityReceiver;


/**
 * Created by am on 1/23/2017.
 */

public class AppController extends Application{

    private static final String TAG = AppController.class.getSimpleName();
    public static AppController mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();

    }

    public static synchronized AppController getInstance(){
        return mInstance;
    }

    private RequestQueue getRequestQueue(){
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public void cancelPendingRequest(Object tag){
        getRequestQueue().cancelAll(tag);
    }

    public static Context getAppContext(){
        return AppController.mContext;
    }


    public void setConnectivityReceiver(ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
