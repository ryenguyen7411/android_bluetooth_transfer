package com.android.rye.bluetoothtransfer.helper;

import com.android.rye.bluetoothtransfer.data.RFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ryeng on 11/18/2016.
 */

public class FileHelper {

    public static ArrayList<RFile> GetFiles(String _path)
    {
        ArrayList<RFile> files = new ArrayList<RFile>();

        File folder = new File(_path);

        if(!folder.isDirectory())
            return null;

        File[] listOfFiles = folder.listFiles();

        for(int i = 0; i < listOfFiles.length; i++)
        {
            files.add(new RFile(listOfFiles[i].getName(), listOfFiles[i].getAbsolutePath()));
        }

        return files;
    }

}