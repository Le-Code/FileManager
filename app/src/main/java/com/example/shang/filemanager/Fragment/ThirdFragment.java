package com.example.shang.filemanager.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.shang.filemanager.BaseApplication;
import com.example.shang.filemanager.Fragment.imagePager.BasePager;
import com.example.shang.filemanager.Fragment.imagePager.ImageSort;
import com.example.shang.filemanager.R;

import java.util.LinkedList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment {

    private LinearLayout ll_container;
    private BasePager pager;
    private LinkedList<View>viewList = new LinkedList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(BaseApplication.getContext(),R.layout.fragment_three,null);
        ll_container = (LinearLayout) view.findViewById(R.id.ll_container);
        pager = new ImageSort(this);
        ll_container.addView(pager.rootView);
        return view;
    }

    public void onChangeView(View view) {
        viewList.add(view);
        ll_container.removeAllViews();
        ll_container.addView(view);
    }

    public void resume(){
        ll_container.removeAllViews();
        ll_container.addView(pager.rootView);
        viewList.clear();
    }

    public void backOnce(){
        ll_container.removeAllViews();
        viewList.removeLast();
        ll_container.addView(viewList.getLast());
    }

}
