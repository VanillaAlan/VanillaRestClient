package com.hjbalan.vanillarest;

import android.content.Context;
import android.content.Intent;

/**
 * Created by alan on 15/1/25.
 */
public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final String EXTRA_LOG = "log";

    public static final String EXTRA_FILE_PATH = "file_path";

    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    private String mFilePath;

    private Context mContext;

    public CustomUncaughtExceptionHandler(Context context, String filePath) {
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        mContext = context;
        mFilePath = filePath;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        String log = LogAnalyse.readException(throwable);
        String fullPath = LogAnalyse.writeLogIntoFile(mContext, mFilePath, log);
        startReportLogActivity(fullPath, log);
        mDefaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
    }

    private void startReportLogActivity(String fullPath, String log) {
        Intent intent = new Intent();
        intent.setAction("com.hjbalan.app.SEND_LOG");
        intent.putExtra(EXTRA_LOG, log);
        intent.putExtra(EXTRA_FILE_PATH, fullPath);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
