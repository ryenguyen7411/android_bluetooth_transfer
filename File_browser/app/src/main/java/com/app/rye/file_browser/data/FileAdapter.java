package com.app.rye.file_browser.data;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rye.file_browser.R;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ryeng on 12/7/2016.
 */

public class FileAdapter extends ArrayAdapter<File> {

    class Tuple {
        public Integer first;
        public File second;
        public View third;

        public Tuple(Integer first, File second, View third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

    Activity context;
    //ArrayList<Pair<Integer, File>>      m_selectedFiles;

    ArrayList<Tuple> m_selectedFiles;


    boolean     m_isSelectedMode;
    boolean     m_isShareable;

    public FileAdapter(Activity context, int layoutId, List<File> objects) {
        super(context, layoutId, objects);
        this.context = context;

        m_isSelectedMode = false;
        m_isShareable = false;

        m_selectedFiles = new ArrayList<Tuple>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item, null, false);
        }

        File file = getItem(position);

        ImageView fileIcon = (ImageView) convertView.findViewById(R.id.icon);
        if(file.isDirectory()) {
            fileIcon.setImageResource(R.drawable.icon_folder);
        } else {
            String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
            int iconId = R.drawable.icon_file_unknown;

            switch(extension) {
                case "mp3":
                    iconId = R.drawable.icon_file_mp3;
                    break;
                case "pdf":
                    iconId = R.drawable.icon_file_pdf;
                    break;
                case "doc":
                case "docx":
                    iconId = R.drawable.icon_file_word;
                    break;
                case "xls":
                case "xlsx":
                    iconId = R.drawable.icon_file_excel;
                    break;
                case "ppt":
                case "pptx":
                    iconId = R.drawable.icon_file_powerpoint;
                    break;
                case "rar":
                case "zip":
                case "7z":
                    iconId = R.drawable.icon_file_zip;
                    break;
                case "png":
                case "jpg":
                case "bmp":
                    iconId = R.drawable.icon_file_image;
                default:
                    break;
            }

            Drawable image = ContextCompat.getDrawable(context, iconId);
            fileIcon.setImageDrawable(image);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        tvName.setText(file.getName());

        TextView tvSize = (TextView) convertView.findViewById(R.id.tv_size);
        tvSize.setText("0 items");

        if(file.isDirectory()) {
            int count = file.listFiles().length;
            tvSize.setText(count + " item" + (count == 1 ? "s" : ""));
        } else {
            tvSize.setText(file.length() + " byte.");
        }

        TextView tvDateModified = (TextView) convertView.findViewById(R.id.tv_dateModified);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        tvDateModified.setText(format.format(file.lastModified()));

        return convertView;
    }

    public boolean isSelectedMode() { return m_isSelectedMode; }
    public void toggleSelectedMode() { m_isSelectedMode = !m_isSelectedMode; }

    public void unselectAll() {
        while(m_selectedFiles.size() > 0) {
            Tuple item = m_selectedFiles.get(0);
            toggleCurrentFile(item.third, item.first, item.second);
        }
    }

    public void deleteAllSelected() {
        while(m_selectedFiles.size() > 0) {
            Tuple item = m_selectedFiles.get(0);
            this.remove(this.getItem(item.first));

            m_selectedFiles.remove(0);
        }
    }

    public boolean isShareable() { return m_isShareable; }
    public ArrayList<File> getSelectedFile() {
        ArrayList<File> files = new ArrayList<File>();

        for(int i = 0; i < m_selectedFiles.size(); i++) {
            files.add(m_selectedFiles.get(i).second);
        }

        return files;
    }

    public void toggleCurrentFile(View v, int position, File currentItem) {
        boolean wasSelected = false;

        for(int i = 0; i < m_selectedFiles.size(); i++) {
            if(m_selectedFiles.get(i).first == position) {
                wasSelected = true;
                m_selectedFiles.remove(i);
                break;
            }
        }

        if(!wasSelected) m_selectedFiles.add(new Tuple(position, currentItem, v));
        v.setBackgroundColor(!wasSelected ? Color.LTGRAY : Color.WHITE);

        if(m_selectedFiles.size() == 0) m_isSelectedMode = false;


        m_isShareable = true;
        for(int i = 0; i < m_selectedFiles.size(); i++) {
            if(!m_selectedFiles.get(i).second.isFile()) {
                m_isShareable = false;
                break;
            }
        }
    }
}
