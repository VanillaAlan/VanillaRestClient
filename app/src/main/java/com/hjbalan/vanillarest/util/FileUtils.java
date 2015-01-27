package com.hjbalan.vanillarest.util;

import com.hjbalan.vanillarest.ApplicationController;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by alan on 15/1/22.
 */
public class FileUtils {

    private static final int BUFFER_SIZE = 8 * 1024; // 8 KB

    /**
     * 写入文件到内部存储
     *  
     * @param data
     * @param fileName
     * @throws IOException
     */
    public static void writeInternalFile(String data, String fileName) throws IOException {
        FileOutputStream fout = ApplicationController.getInstance().openFileOutput(fileName,
                Context.MODE_PRIVATE);
        fout.write(data.getBytes());
        fout.close();
    }

    /**
     * 读取内部存储文件
     *  
     * @param fileName
     * @return the file content as string format
     * @throws IOException
     */
    public static String readInternalFileAsString(String fileName) throws IOException {
        FileInputStream fin = ApplicationController.getInstance().openFileInput(fileName);
        int length = fin.available();
        byte[] buffer = new byte[length];
        fin.read(buffer);
        fin.close();
        return new String(buffer, Charset.forName("UTF-8"));
    }

    /**
     * 删除指定内部存储文件
     *  
     * @param fileName
     * @return
     */
    public static boolean deleteInternalFile(String fileName) {
        String path = ApplicationController.getInstance().getFilesDir() + "/" + fileName;
        File file = new File(path);
        if (file.exists()) {
            if (file.delete()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 写入字符串到指定文件
     *
     * @param data
     * @param file
     * @throws IOException
     */
    public static void writeFile(String data, File file) throws IOException {
        writeFile(data.getBytes(Charset.forName("UTF-8")), file);
    }

    /**
     * 写入字节数组数据到指定文件
     *
     * @param data
     * @param file
     * @throws IOException
     */
    public static void writeFile(byte[] data, File file) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file, false));
        bos.write(data);
        bos.close();
    }

    /**
     * 读取指定文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String readFileAsString(File file) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        copyStream(bis, bos);
        byte[] contents = bos.toByteArray();
        bis.close();
        bos.close();
        return new String(contents, Charset.forName("UTF-8"));
    }

    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        while (true) {
            int count = is.read(bytes, 0, BUFFER_SIZE);
            if (count == -1) {
                break;
            }
            os.write(bytes, 0, count);
        }
    }
}
