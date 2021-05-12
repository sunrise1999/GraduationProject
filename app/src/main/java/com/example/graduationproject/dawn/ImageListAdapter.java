package com.example.graduationproject.dawn;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.graduationproject.R;

import java.util.List;

class ViewHolder {
    public ImageView imgThumb;
    public TextView tv_imgFileName;
    public TextView tv_imgFilePath;
    public TextView tv_imgDateModified;

    View itemView;

    public ViewHolder(View itemView) {
        if (itemView == null) {
            throw new IllegalArgumentException("itemView can not be null");
        }
        this.itemView = itemView;
        imgThumb = (ImageView) itemView.findViewById(R.id.img_thumb);
        tv_imgFileName = (TextView) itemView.findViewById(R.id.img_filename);
        tv_imgFilePath = (TextView) itemView.findViewById(R.id.img_filepath);
        tv_imgDateModified = (TextView) itemView.findViewById(R.id.img_datemodified);
    }
}

public class ImageListAdapter extends BaseAdapter {
    private List<ImageInfo> imgList;
    private LayoutInflater layoutInflater;
    private Context context;
    private int currentPos;
    private com.example.graduationproject.dawn.ViewHolder holder = null;

    public ImageListAdapter(Context context, List<ImageInfo> imgList) {
        this.imgList = imgList;
        this.context = context;

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int position) {
        return imgList.get(position).getFileName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int index) {
        imgList.remove(index);
    }

    public void refreshDataSet() {
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            holder = new com.example.graduationproject.dawn.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (com.example.graduationproject.dawn.ViewHolder) convertView.getTag();
        }
        holder.imgThumb.setImageBitmap(
                BitmapFactory.decodeFile(imgList.get(position).getFilePath())
        );
        holder.tv_imgFileName.setText(imgList.get(position).getFileName());
        holder.tv_imgFilePath.setText(imgList.get(position).getFilePath());
        holder.tv_imgDateModified.setText(imgList.get(position).getFileDate());

        return convertView;
    }
}
