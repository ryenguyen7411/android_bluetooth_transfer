package com.android.rye.bluetoothtransfer.helper;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.android.rye.bluetoothtransfer.data.RFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ryeng on 11/18/2016.
 */

public class FileHelper {

    private static final String TAG = "FileHelper";
    public static ArrayList<RFile> GetFiles(String path)
    {
        ArrayList<RFile> files = new ArrayList<RFile>();

        File folder = new File(path);

        if(!folder.isDirectory())
            return null;

        File[] listOfFiles = folder.listFiles();

        files.add(new RFile("..", folder.getParent()));
        for(int i = 0; i < listOfFiles.length; i++)
        {
            files.add(new RFile(listOfFiles[i].getName(), listOfFiles[i].getAbsolutePath()));
        }

        return files;
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
            Log.e("Open File", e.getMessage());
        }

        return null;
    }
}
