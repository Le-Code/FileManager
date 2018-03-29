package com.example.shang.filemanager.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.shang.filemanager.BaseApplication;
import com.example.shang.filemanager.Fragment.sendPager.MainPager;
import com.example.shang.filemanager.R;

/**
 * Created by Shang on 2017/7/24.
 * 显示音乐
 */
public class FiveFragment extends Fragment{

    private MainPager mainPager;
    private LinearLayout ll_five_container;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = View.inflate(BaseApplication.getContext(),R.layout.fragment_five,null);
        ll_five_container = (LinearLayout) view.findViewById(R.id.ll_five_container);
        mainPager = new MainPager(this);
        onChangeView(mainPager.rootView);
        return view;
    }

    public void onChangeView(View view) {
        ll_five_container.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        ll_five_container.addView(view);
    }

    public void resume(){
        ll_five_container.removeAllViews();
        ll_five_container.addView(mainPager.rootView);
    }
}
