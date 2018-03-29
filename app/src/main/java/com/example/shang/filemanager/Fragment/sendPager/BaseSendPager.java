package com.example.shang.filemanager.Fragment.sendPager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.example.shang.filemanager.BaseApplication;


/**
 * Created by yaojian on 2017/10/24.
 */

public abstract class BaseSendPager {
    public Fragment fragment;
    public Context mContext;
    public View rootView;

    public BaseSendPager(Fragment fragment){
        this.fragment = fragment;
        this.mContext = BaseApplication.getContext();
        rootView = initView();
    }

    public abstract View initView();

    public void initDate(){}
}
