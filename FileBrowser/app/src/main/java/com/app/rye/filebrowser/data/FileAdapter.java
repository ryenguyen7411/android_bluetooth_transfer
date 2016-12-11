package com.app.rye.filebrowser.data;

import android.app.Activity;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.rye.filebrowser.R;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
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

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        tvName.setText(file.getName());

        TextView tvSize = (TextView) convertView.findViewById(R.id.tv_size);
        tvSize.setText("0 items");

        TextView tvDateModified = (TextView) convertView.findViewById(R.id.tv_dateModified);
        tvDateModified.setText("1 minute ago");

        return convertView;
    }

    public boolean isSelectedMode() { return m_isSelectedMode; }
    public void toggleSelectedMode() { m_isSelectedMode = !m_isSelectedMode; }

    public void selectAll(boolean selected) {
        for(int i = 0; i < m_selectedFiles.size(); i++) {

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
