package com.android.rye.bluetoothtransfer.data;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.rye.bluetoothtransfer.R;

import java.util.List;

/**
 * Created by ryeng on 11/19/2016.
 */

public class RFileAdapter extends ArrayAdapter<RFile> {
    Activity context;

    boolean     m_isCheckable;

    public RFileAdapter(Activity context, int layoutId, List<RFile> objects) {
        super(context, layoutId, objects);
        this.context = context;

        m_isCheckable = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.rfile_item, null, false);
        }

        RFile file = getItem(position);

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.rfileCheckBox);
        checkBox.setVisibility(m_isCheckable == true ? View.VISIBLE : View.GONE);

        TextView tvName = (TextView) convertView.findViewById(R.id.rfileName);
        tvName.setText(file.getName());

        return convertView;
    }

    public void setCheckBoxCheckable(boolean checkable) {
        m_isCheckable = checkable;
    }
}
