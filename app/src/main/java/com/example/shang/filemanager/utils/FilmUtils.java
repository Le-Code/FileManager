package com.example.shang.filemanager.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;


import com.example.shang.filemanager.entity.FilmInfo;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaojian on 2017/7/31.
 */

public class FilmUtils {

    public static List<FilmInfo> getFilmList(Context context){
        List<FilmInfo> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,null,null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()){
            FilmInfo info = new FilmInfo();
            info.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
            info.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
            info.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
            info.setSize(sizeToFotmat(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))));
            info.setDate(dateToFormat(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))));
            list.add(info);
        }
        return list;
    }

    public static String dateToFormat(long date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    public static String sizeToFotmat(long size){
        double G;
        int M;
        DecimalFormat format = new DecimalFormat("#.0");
        M = (int) (size/1024/1024);
        if (M>=1024){
            G = M/1024.0;
            return format.format(G)+"G";
        }
        return M+"M";
    }

    public static void deleteFilm(Context context, String path){
        File file = new File(path);
        Log.i("TEST",path);
        if (file.exists()){
            if(file.delete())
                context.getContentResolver().delete(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,"_data=?",new String[]{path}
                );
        }
    }
}
