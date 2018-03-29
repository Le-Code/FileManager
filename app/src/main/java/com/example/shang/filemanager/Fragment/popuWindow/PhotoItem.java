package com.example.shang.filemanager.Fragment.popuWindow;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.shang.filemanager.R;

import java.io.File;

/**
 * Created by yaojian on 2017/11/9.
 */

public class PhotoItem extends MediaItem {

    private ImageView imageView;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.popu_photo_item,null);
        imageView = (ImageView) view.findViewById(R.id.iv_photo);
        return view;
    }

    @Override
    public void initDate(File file, int x, int y) {
        Glide.with(mContext).load(file.getAbsolutePath()).override(x,y).fitCenter().into(imageView);
    }

}
