package com.hjbalan.vanillarest.volley.cache;

import com.android.volley.toolbox.ImageLoader;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by alan on 15/1/26.
 */
public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        return cacheSize;
    }

    public BitmapLruCache() {
        this(getDefaultLruCacheSize());
    }

    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public Bitmap getBitmap(String s) {
        return get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        put(s, bitmap);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }
}
