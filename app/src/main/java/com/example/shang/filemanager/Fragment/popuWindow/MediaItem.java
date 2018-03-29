package com.example.shang.filemanager.Fragment.popuWindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter;
import com.example.shang.filemanager.BaseApplication;

import java.io.File;

/**
 * Created by yaojian on 2017/11/9.
 */

public abstract class MediaItem {

    public View rootView;
    public Context mContext;

    public MediaItem(){
        this.mContext = BaseApplication.getContext();
        this.rootView = initView();
    }

    public abstract View initView();

    public void initDate(File file, int x, int y){}

    public void close(){}
}
