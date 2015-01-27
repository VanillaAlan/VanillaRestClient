package com.hjbalan.vanillarest.api.tool;

import com.hjbalan.vanillarest.ApplicationController;

import android.content.pm.PackageInfo;
import android.os.Build;

import java.util.Map;

/**
 * Created by alan on 15/1/23.
 */
public class ApiUtils {

    private ApiUtils() {
    }

    public static void buildDefaultHearders(Map<String, String> headers) {
        String packageName = ApplicationController.getInstance().getPackageName();
        PackageInfo packageInfo = ApplicationController.getInstance().getPackageInfo();
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER)) {
            model = Build.MANUFACTURER + " " + model;
        }
        String userAgent = packageInfo == null ? packageName :
                packageName + "/" + packageInfo.versionCode + "/" + model + "/"
                        + Build.VERSION.RELEASE;
        headers.put("User-Agent", userAgent);
    }

}
