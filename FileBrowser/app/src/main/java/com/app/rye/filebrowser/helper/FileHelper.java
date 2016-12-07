package com.app.rye.filebrowser.helper;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
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
}
