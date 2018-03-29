package com.example.shang.filemanager.Fragment.imagePager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.example.shang.filemanager.BaseApplication;

/**
 * Created by yaojian on 2017/10/20.
 */

public abstract class BasePager {

    public Fragment fragment;
    public Context mContext;
    public View rootView;

    public BasePager(Fragment fragment){
        this.fragment = fragment;
        mContext = BaseApplication.getContext();
        rootView = initView();
    }

    public abstract View initView();
    public void initDate(int id){}

}
