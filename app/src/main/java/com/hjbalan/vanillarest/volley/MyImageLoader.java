package com.hjbalan.vanillarest.volley;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by alan on 15/1/26.
 */
public class MyImageLoader {

    private MyImageLoader() {
    }

    public static void loadImage(String url, ImageView imageView) {
        ImageLoader imageLoader = VolleyManager.getInstance().getImageLoader();
        imageLoader.get(url, new DefaultImageListener(imageView));
    }

    public static void loadImage(String url, ImageLoader.ImageListener imageListener) {
        ImageLoader imageLoader = VolleyManager.getInstance().getImageLoader();
        imageLoader.get(url, imageListener);
    }

    public static void loadImage(String url, ImageView imageView, int loadingImage,
            int errorImage) {
        ImageLoader imageLoader = VolleyManager.getInstance().getImageLoader();
        imageLoader.get(url, ImageLoader.getImageListener(imageView, loadingImage, errorImage));
    }
    
    public static class DefaultImageListener implements ImageLoader.ImageListener {

        public static final int FADE_IN_TIME_MS = 250;

        private ImageView mImageView;

        public DefaultImageListener(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            Bitmap bitmap = response.getBitmap();
            if (bitmap == null) {
                return;
            }
            mImageView.setImageBitmap(bitmap);
            if (!isImmediate) {
                ObjectAnimator.ofFloat(mImageView, "alpha", 0, 1).setDuration(FADE_IN_TIME_MS)
                        .start();
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }

}
