package com.android.rye.bluetoothtransfer.data;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rye.bluetoothtransfer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryeng on 11/19/2016.
 */

public class RFileAdapter extends ArrayAdapter<RFile> {
    Activity context;

    boolean             m_isCheckable;
    ArrayList<RFile>    m_checkedList;

    boolean             m_isShareable;

    public RFileAdapter(Activity context, int layoutId, List<RFile> objects) {
        super(context, layoutId, objects);
        this.context = context;

        m_isCheckable = false;
        m_checkedList = new ArrayList<RFile>();

        m_isShareable = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.rfile_item, null, false);
        }

        RFile file = getItem(position);

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.rfileCheckBox);
        checkBox.setVisibility(m_isCheckable == true ? View.VISIBLE : View.GONE);

//        ImageView icon = (ImageView) convertView.findViewById(R.id.rfileIcon);
//        String uri = "mipmap/" + file.getThumbnail();
//        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
//        Drawable image = ContextCompat.getDrawable(context, imageResource);
//        icon.setImageDrawable(image);

        TextView tvName = (TextView) convertView.findViewById(R.id.rfileName);
        tvName.setText(file.getName());

        return convertView;
    }

    public void setCheckBoxCheckable(boolean checkable) {
        m_isCheckable = checkable;
        notifyDataSetChanged();
    }

    public void toggleCurrentRFile(View view, boolean checked, RFile currentItem) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.rfileCheckBox);
        checkBox.setChecked(checked);

        if(checked == true) {
            m_checkedList.add(currentItem);
        } else {
            m_checkedList.remove(currentItem);
        }

        if(m_checkedList.size() == 0)
            m_isShareable = false;
        else {
            m_isShareable = true;
            for(int i = 0; i< m_checkedList.size(); i++) {
                if(!m_checkedList.get(i).isFile()) {
                    m_isShareable = false;
                    break;
                }
            }
        }
    }

    public boolean isChecked(View view) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.rfileCheckBox);
        return checkBox.isChecked();
    }

    public boolean isCheckable() {
        return m_isCheckable;
    }

    public ArrayList<RFile> getCheckedList() { return m_checkedList; }

    public boolean isShareable() { return m_isShareable; }
}
