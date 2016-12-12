package com.app.rye.file_browser.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.app.rye.file_browser.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ryeng on 12/7/2016.
 */

public class FileHelper {
    private static final String TAG = "FileHelper";
    public static ArrayList<File> GetFiles(String path)
    {
        File folder = new File(path);

        if(!folder.isDirectory())
            return null;

        File[] listOfFiles = folder.listFiles();

        return new ArrayList<File>(Arrays.asList(listOfFiles));
    }

    public static Intent OpenFile(String path) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            File file = new File(path);
            String fileExt = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt);

            intent.setDataAndType(Uri.fromFile(file), mimeType);

            return intent;

        } catch(Exception e) {
            Log.e("Open File error", e.getMessage());
        }

        return null;
    }

    public static void CopyFiles(ArrayList<File> files, String outputPath, Activity activity) {
        InputStream in;
        OutputStream out;

        boolean isApplyAll = false;
        int func = 0;       // 0: Cancel, 1: Rename, 2: Replace

        try {
            for(int i = 0; i < files.size(); i++) {
                File file = files.get(i);

//                File temp = new File(outputPath + "/" + file.getName());
//                if(temp.exists()) {
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity)
//                            .setTitle("Confirmation")
//                            .setMessage(file.getName() + " cannot be copied. There is already a file with this name in destination folder.");
//                } else {
//
//                }

                if(file.isFile()) {
                    in = new FileInputStream(file.getPath());
                    out = new FileOutputStream(outputPath + "/" + file.getName());

                    byte[] buffer = new byte[1024];
                    int read;
                    while((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();

                    out.flush();
                    out.close();
                } else if (file.isDirectory()) {
                    CreateFolder(outputPath + "/" + file.getName());
                    CopyFiles(FileHelper.GetFiles(file.getPath()), outputPath + "/" + file.getName(), activity);
                }
            }
        } catch (Exception e) {
            Log.e("Copy Files", e.getMessage());
        }
    }

    public static void MoveFiles(ArrayList<File> files, String outputPath) {
        InputStream in;
        OutputStream out;

        try {
            for(int i = 0; i < files.size(); i++) {
                File file = files.get(i);

                if(file.isFile()) {
                    in = new FileInputStream(file.getPath());
                    out = new FileOutputStream(outputPath + "/" + file.getName());

                    byte[] buffer = new byte[1024];
                    int read;
                    while((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();

                    out.flush();
                    out.close();

                    file.delete();
                } else if (file.isDirectory()) {
                    CreateFolder(outputPath + "/" + file.getName());
                    MoveFiles(FileHelper.GetFiles(file.getPath()), outputPath + "/" + file.getName());
                }
            }
        } catch (Exception e) {
            Log.e("Move Files", e.getMessage());
        }
    }

    public static void DeleteFiles(ArrayList<File> files) {
        try {
            for(int i = 0; i < files.size(); i++) {
                File file = files.get(i);

                if (file.isDirectory()) {
                    DeleteFiles(FileHelper.GetFiles(file.getPath()));
                }

                file.delete();
            }
        } catch (Exception e) {
            Log.e("Delete Files", e.getMessage());
        }
    }

    public static void CreateFolder(String path) {
        try {
            File folder = new File(path);

            boolean success = true;
            if(!folder.exists()) {
                if (folder.mkdirs()) success = true;
                else success = false;
            }
        } catch (Exception e) {
            Log.e("Create folder", e.getMessage());
        }
    }
}