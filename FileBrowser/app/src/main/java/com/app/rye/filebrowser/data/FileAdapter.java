package com.app.rye.filebrowser.data;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.rye.filebrowser.R;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

/**
 * Created by ryeng on 12/7/2016.
 */

public class FileAdapter extends ArrayAdapter<File> {

    Activity context;

    boolean     m_isSelectedMode;

    public FileAdapter(Activity context, int layoutId, List<File> objects) {
        super(context, layoutId, objects);
        this.context = context;

        m_isSelectedMode = false;
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
}
