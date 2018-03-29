package com.example.shang.filemanager.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.shang.filemanager.R;
import com.example.shang.filemanager.entity.MusicInfo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaojian on 2017/7/31.
 */

public class MusicUtils {

//    public static int PAGE_COUNT = 50;

    public static List<MusicInfo> getMusicList(Context context, int page){
        List<MusicInfo> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//        int start = (page-1)*PAGE_COUNT;
//        int end = page*PAGE_COUNT;
        if (cursor.moveToFirst()){
            do{
                MusicInfo info = new MusicInfo();
                info.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                info.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                info.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                info.setDuration(durationToFormat(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                info.setSize(sizeToFormat(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))));
                info.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                info.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                info.setBitmapDrawable(getBitmapDrawable(context,
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))));
                list.add(info);
//                start++;
            }while (cursor.moveToNext());
        }
        return list;
    }

    public static String sizeToFormat(int size){
        double Mb = size/1024.0/1024.0;
        DecimalFormat format = new DecimalFormat("#.00");
        return format.format(Mb)+"M";
    }

    public static String durationToFormat(int duration){
        int mm,ss;
        int S = duration/1000;
        ss = S%60;
        mm = S/60;
        return mm+":"+ss;
    }

    public static void deleteMusic(Context context, String path){
        File file = new File(path);
        Log.i("TEST",path);
        if (file.exists()){
            if(file.delete())
                context.getContentResolver().delete(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,"_data=?",new String[]{path}
                );
        }
    }

    public static BitmapDrawable getBitmapDrawable(Context context, int album_id){
        String album_art = null;
        String uri = "content://media/external/audio/albums";
        Cursor cursor = context.getContentResolver().query(
                Uri.parse(uri+"/"+ String.valueOf(album_id)),
                new String[]{"album_art"},null,null,null);
        if (cursor.getColumnCount()>0&&cursor.getCount()>0){
            cursor.moveToFirst();
            album_art = cursor.getString(0);
        }
        cursor.close();
        Bitmap bitmap = null;
        if (album_art!=null){
            bitmap = BitmapFactory.decodeFile(album_art);
        }else{
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_music_img);
        }
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }
}
