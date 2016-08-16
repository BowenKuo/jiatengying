package org.onlineservice.rand.login;

/**
 *  Import
 */
import android.app.Application;
import android.text.TextUtils;
// External Libs
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Objects;

public class AppControl extends Application{
    //Get the Class name
    public static final java.lang.String TAG = AppControl.class.getSimpleName();
    //Variables for Volley
    private RequestQueue mRequestQuene;
    private static AppControl mInstance;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }
    //Synchronize the mInstance
    public static synchronized AppControl getInstance(){
        return mInstance;
    }

    public RequestQueue getmRequestQuene(){
        if (mRequestQuene == null){
            mRequestQuene = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQuene;
    }

    public <T> void addToRequestQuene(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getmRequestQuene().add(req);
    }

    public <T> void addToRequestQuene(Request<T> req){
        req.setTag(TAG);
        getmRequestQuene().add(req);
    }

    public void cancelPendingRequest(Object tag){
        if (mRequestQuene != null){
            mRequestQuene.cancelAll(tag);
        }
    }

}
