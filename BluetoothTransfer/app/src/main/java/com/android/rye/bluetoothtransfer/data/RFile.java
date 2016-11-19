package com.android.rye.bluetoothtransfer.data;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ryeng on 11/18/2016.
 */

public class RFile {
    private String m_filePath;
    private String m_fileName;
    private long m_fileSize;
    private String m_dateCreated;

    public RFile(String fileName, String filePath) {
        this.m_fileName = fileName;
        this.m_filePath = filePath;
    }

    public String getName() {
        return m_fileName;
    }

    public String getPath() {
        return m_filePath;
    }
}
