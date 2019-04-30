package com.ub.techexcel.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

public class FileUtils {
    /**
     * sd卡的根目录
     */
    private static String mSdRootPath = Environment
            .getExternalStorageDirectory().getPath();
    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;
    /**
     * 保存Image的目录名
     */
    private final static String FOLDER_NAME = "/.ubao";
    private final static String FOLDER_NAME2 = "/.ubao2";

    /**
     * getCacheDir()方法用于获取/data/data//cache目录
     * <p>
     * getFilesDir()方法用于获取/data/data//files目录
     *
     * @param context
     */
    public FileUtils(Context context) {
        mDataRootPath = context.getCacheDir().getPath();
    }

    /**
     * 获取储存Image的目录 Environment.getExtemalStorageState()
     * <p>
     * Environment.MEDIA_MOUNTED手机装有SDCard,并且可以进行读写
     * <p>
     * 获取SDCard的目录:Environment.getExtemalStorageDirectory()
     *
     * @return
     */
    public String getStorageDirectory() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME
                : mDataRootPath + FOLDER_NAME;
    }
    public String getStorageDirectory2() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME2
                : mDataRootPath + FOLDER_NAME2;
    }

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     *
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public void savaBitmap(String fileName, Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return;
        }
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdir(); // 创建文件目录
        }
        File file = new File(path + File.separator + fileName + ".png");
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file); // 文件输出流 字节流
        // 将图片保存到文件中
        bitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }

    /**
     * 从手机或者sd卡获取Bitmap
     *
     * @param fileName
     * @return
     */
    public Bitmap getBitmap(String fileName) {
        return BitmapFactory.decodeFile(getStorageDirectory() + File.separator
                + fileName + ".png");
    }

    /**
     * 判断文件是否存在
     *
     * @return
     */
    public boolean isFileExists(String fileurl) {
        return new File(fileurl).exists();
    }

    /**
     * 获取文件的大小
     *
     * @param fileName
     * @return
     */
    public long getFileSize(String fileName) {
        return new File(getStorageDirectory() + File.separator + fileName
                + ".png").length();
    }

    /**
     * 删除SD卡或者手机的缓存图片和目录
     */
    public void deleteFile() {
        File dirFile = new File(getStorageDirectory());
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(dirFile, children[i]).delete();
            }
        }
        dirFile.delete();
    }

    public void deleteFile2() {
        File dirFile = new File(getStorageDirectory2());
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(dirFile, children[i]).delete();
            }
        }
        dirFile.delete();
    }


    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it 
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
