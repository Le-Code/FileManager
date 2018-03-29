package com.example.shang.filemanager.utils;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;

import com.example.shang.filemanager.entity.PhotoInformation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by yaojian on 2017/10/12.
 */

public class ImageFind {

    public Context mContext;
    private List<PhotoInformation>photoInformationList;
    public ImageFind(Context mContext){
        this.mContext = mContext;
        this.photoInformationList = getPhotos();
    }

    public List<PhotoInformation>getGif(String regex){
        List<PhotoInformation>wantList = new ArrayList<>();
        for (PhotoInformation info:photoInformationList){
            if (info.getName().matches(regex)){
                wantList.add(info);
            }
        }
        return wantList;
    }

    public List<PhotoInformation>getFilterList(String regex){
        List<PhotoInformation>wantList = new ArrayList<>();
        for (PhotoInformation info:photoInformationList){
            if (info.getPath().matches(regex)){
                wantList.add(info);
            }
        }
        return wantList;
    }

    public List<PhotoInformation> getPhotos() {
        List<PhotoInformation>list = new ArrayList<>();
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, "datetaken desc");
        while (cursor.moveToNext()) {
            // 获取图片的名称
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            // 获取图片的生成路径
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            // 获取图片的详细日期
            String date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
            // 格式化时间
            Date time = new Date(Long.parseLong(date));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.format(time);
            // 格式化文件大小
            String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            size = Formatter.formatFileSize(mContext, Long.parseLong(size));
            // 文件类型
            String type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
            // 经纬度
            String LATITUDE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
            String LONGITUDE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
            String location = null;
            // 根据经纬度查询拍照地点
            if (LATITUDE != null && LONGITUDE != null) {
                Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(LATITUDE),
                            Double.parseDouble(LONGITUDE), 1);
                    StringBuilder builder = new StringBuilder();
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        builder.append(address.getAddressLine(0)).append("\n");
                        location = builder.toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            PhotoInformation information = new PhotoInformation(name, path, date, size, type, location);
            list.add(information);
            Log.i("test",information.getName());
        }
        return list;
    }
}
