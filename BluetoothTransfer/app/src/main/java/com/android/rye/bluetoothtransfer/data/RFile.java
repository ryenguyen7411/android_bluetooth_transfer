package com.android.rye.bluetoothtransfer.data;

import android.view.View;
import android.view.ViewGroup;

import java.io.File;

/**
 * Created by ryeng on 11/18/2016.
 */

public class RFile {
    private String m_filePath;
    private String m_fileName;
    private long m_fileSize;
    private String m_dateCreated;

    private boolean         m_isFile;

    public RFile(String fileName, String filePath) {
        this.m_fileName = fileName;
        this.m_filePath = filePath;

        File file = new File(this.m_filePath);
        m_isFile = file.isFile();
    }

    public String getName() {
        return m_fileName;
    }

    public String getPath() {
        return m_filePath;
    }

    public boolean isFile() { return m_isFile; }
}
