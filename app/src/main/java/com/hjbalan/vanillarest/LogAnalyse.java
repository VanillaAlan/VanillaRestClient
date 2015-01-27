package com.hjbalan.vanillarest;;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by alan on 15/1/26.
 */
public class LogAnalyse {

    public static final String FILE_SUFFIX = ".log";

    private static DateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private LogAnalyse() {

    }

    /**
     * 将异常读取到字符串
     *
     * @return 字符串日志
     */
    public static String readException(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String log = writer.toString();
        try {
            writer.close();
        } catch (IOException e) {
            Timber.e("read exception %s ", e.toString());
        }

        return log;
    }

    /**
     * 写入日志到指定文件
     *
     * @return 文件的全路径
     */
    public static String writeLogIntoFile(Context context, String filePath, String log) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER)) {
            model = Build.MANUFACTURER + " " + model;
        }

        // Make file name - file must be saved to external storage or it wont be readable by
        // the email app.
        String fullPath = filePath + sDateFormat.format(new Date()) + FILE_SUFFIX;

        File file = new File(fullPath);
        try {
            FileWriter filewriter = new FileWriter(file);
            filewriter.append("Android version: " + Build.VERSION.SDK_INT + "\n");
            filewriter.append("Device: " + model + "\n");
            filewriter.append("App version: " + (info == null ? "(null)" : info.versionCode)
                    + "\n\n");
            filewriter.append(log);
            filewriter.close();
        } catch (IOException e) {
            Timber.e("write log file exception:%s", e.toString());
        }
        return fullPath;
    }

    /**
     * 写入异常信息到指定文件
     */
    public static String writeExceptionIntoFile(Context context, String filePath,
            Throwable throwable) {
        String log = LogAnalyse.readException(throwable);
        String fullPath = LogAnalyse.writeLogIntoFile(context, filePath, log);
        return fullPath;
    }
}
