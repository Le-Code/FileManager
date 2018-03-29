package com.example.shang.filemanager.Fragment.popuWindow;

import android.view.View;
import android.widget.VideoView;

import com.example.shang.filemanager.R;

import java.io.File;

/**
 * Created by yaojian on 2017/11/9.
 */

public class FilmItem extends MediaItem {

    private VideoView mView;

    @Override
    public View initView() {
        View view = View.inflate(mContext,R.layout.popu_film_item,null);
        mView = (VideoView) view.findViewById(R.id.video);
        return view;
    }

    @Override
    public void initDate(File file, int x, int y) {
        mView.setVideoPath(file.getAbsolutePath());
        mView.start();
    }

    @Override
    public void close() {
        super.close();
    }
}
