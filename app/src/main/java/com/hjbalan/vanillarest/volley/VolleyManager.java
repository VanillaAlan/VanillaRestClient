package com.hjbalan.vanillarest.volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hjbalan.vanillarest.ApplicationController;
import com.hjbalan.vanillarest.volley.cache.BitmapLruCache;

import android.text.TextUtils;
import android.util.DisplayMetrics;

/**
 * Created by alan on 15/1/22.
 */
public class VolleyManager {

    private static final String TAG = "nut_volley";

    private static VolleyManager sInstance = null;

    private RequestQueue mRequestQueue;

    private ImageLoader mImageLoader;

    private VolleyManager() {
        FakeX509TrustManager.allowAllSSL();
        mRequestQueue = Volley
                .newRequestQueue(ApplicationController.getInstance().getApplicationContext());
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(calculateCacheSize()));
    }

    private int calculateCacheSize() {
        DisplayMetrics dm = ApplicationController.getInstance().getApplicationContext()
                .getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        // Assuming an ARGB_8888 pixel format, 4 bytes per pixel
        int size = screenWidth * screenHeight * 4;
        int cacheSize = size * 3;
        return cacheSize;
    }

    public static VolleyManager getInstance() {
        if (sInstance == null) {
            sInstance = new VolleyManager();
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
    
    /**
     * Adds the specified request to the global queue, if tag is specified then
     * it is used else Default TAG is used.
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        mRequestQueue.add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        addToRequestQueue(req, null);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important to
     * specify a TAG so that the pending/ongoing requests can be cancelled.
     */
    public void cancelPendingRequests(Object tag) {
        if (tag == null) {
            return;
        }
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
