package com.hjbalan.vanillarest;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;

import timber.log.Timber;

/**
 * Created by alan on 15/1/20.
 */
public class ApplicationController extends Application implements
        Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    public static final String ROOT_DIR = Environment.getExternalStorageDirectory() + File.separator
            + "VanillaRest/";

    private static final String LOG_DIR = "log/";

    public static boolean sIsInBackground = false;

    private static ApplicationController sInstance;

    private static String sLogFilePath = null;

    private static String sStateOfLifeCycle = "";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        registerActivityLifecycleCallbacks(this);
        initFileDir();
        initLog();
    }

    public static synchronized ApplicationController getInstance() {
        return sInstance;
    }

    public PackageInfo getPackageInfo() {
        String packageName = getPackageName();
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e.toString());
        }
        return packageInfo;
    }

    private void initFileDir() {
        File root = new File(ROOT_DIR);
        if (root.exists()) {
            return;
        }
        root.mkdir();
    }

    private void initLog() {
        initLogFile();
        Thread.setDefaultUncaughtExceptionHandler(
                new CustomUncaughtExceptionHandler(getApplicationContext(), sLogFilePath));
        if (Config.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());

        }
    }

    private void initLogFile() {
        sLogFilePath = ROOT_DIR + LOG_DIR;
        File dir = new File(sLogFilePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        sIsInBackground = false;
        sStateOfLifeCycle = "created";
    }

    @Override
    public void onActivityStarted(Activity activity) {
        sStateOfLifeCycle = "started";
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        sIsInBackground = false;
        sStateOfLifeCycle = "destroyed";
    }

    @Override
    public void onActivityStopped(Activity activity) {
        sStateOfLifeCycle = "stopped";
    }

    @Override
    public void onActivityResumed(Activity activity) {
        sStateOfLifeCycle = "resumed";
    }

    @Override
    public void onActivityPaused(Activity activity) {
        sStateOfLifeCycle = "paused";
    }

    @Override
    public void onTrimMemory(int level) {
        if (sStateOfLifeCycle.equals("stopped")) {
//            app going to background
            sIsInBackground = true;
        }
        super.onTrimMemory(level);

    }

    private static class CrashReportingTree extends Timber.HollowTree {

        @Override
        public void i(String message, Object... args) {
            LogAnalyse.writeLogIntoFile(ApplicationController.getInstance().getApplicationContext(),
                    sLogFilePath, String.format(message, args));
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            i(message, args);
        }

        @Override
        public void e(String message, Object... args) {
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);
            LogAnalyse.writeExceptionIntoFile(
                    ApplicationController.getInstance().getApplicationContext(), sLogFilePath, t);
        }
    }

}
