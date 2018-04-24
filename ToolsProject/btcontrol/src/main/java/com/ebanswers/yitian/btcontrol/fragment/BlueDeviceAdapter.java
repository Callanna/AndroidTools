package com.ebanswers.yitian.btcontrol.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ebanswers.yitian.btcontrol.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2017/3/23.
 */

public class BlueDeviceAdapter extends BaseAdapter{
    private List<BluetoothDevice> stuList = new ArrayList<>();
    private LayoutInflater inflater;
    private ItemClickListener itemClickListener;

    public BlueDeviceAdapter(Context context){
        this.inflater=LayoutInflater.from(context.getApplicationContext());
    }

    public BlueDeviceAdapter(List<BluetoothDevice> list, Context context){
        this.stuList.addAll(list);
        this.inflater=LayoutInflater.from(context);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getCount() {
        return stuList==null?0:stuList.size();
    }

    @Override
    public Object getItem(int position) {
        return stuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ListItemView  listItemView = null;
        if(convertView == null) {
            listItemView = new ListItemView();
            convertView = inflater.inflate(R.layout.layout_device_item, null);
            listItemView.name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }
        listItemView.name.setText(stuList.get(position).getName()+stuList.get(position).getAddress());
        listItemView.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.itemclick(position,stuList.get(position));
                }
            }
        });
        return convertView;
    }
     public void add(BluetoothDevice device){
         stuList.add(device);
         notifyDataSetChanged();
     }

    public void clear() {
        stuList.clear();
        notifyDataSetChanged();
    }


    public final class ListItemView{                //自定义控件集合
        public TextView name;
    }
    public interface ItemClickListener{
        void itemclick(int position,BluetoothDevice device);
    }
}
