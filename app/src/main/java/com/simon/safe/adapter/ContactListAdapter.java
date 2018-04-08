package com.simon.safe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.simon.safe.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 38640 on 2018/4/1.
 */

public class ContactListAdapter extends BaseAdapter {

    private Context mContext;
    private List<HashMap<String, String>> mContactList = null;

    public ContactListAdapter(Context context, List<HashMap<String, String>> contactList) {
        this.mContext = context;
        this.mContactList = contactList;
    }

    @Override
    public int getCount() {
        return mContactList.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return mContactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_contact_item, null);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_name.setText(getItem(position).get("name"));
        viewHolder.tv_phone.setText(getItem(position).get("phone"));

        return convertView;
    }

    class ViewHolder {

        private TextView tv_name;
        private TextView tv_phone;
    }
}
