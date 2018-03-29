package com.example.shang.filemanager.Fragment.imagePager;

import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.example.shang.filemanager.BaseApplication;
import com.example.shang.filemanager.Fragment.ThirdFragment;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.adapter.ImageAdapter;
import com.example.shang.filemanager.entity.PhotoInformation;
import com.example.shang.filemanager.utils.ConstantValue;
import com.example.shang.filemanager.utils.ImageFind;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yaojian on 2017/10/20.
 */

public class ImageShowList extends BasePager implements View.OnClickListener,
        ImageAdapter.OnImageItemClickListener {

    private ImageButton ib_back;
    private RecyclerView rv_show;
    private ImageFind imageFind;
    private List<PhotoInformation> list;
    private ImageAdapter mAdapter;
    private GridLayoutManager layoutManager;

    private ImageShowDetail pager;

    public ImageShowList(Fragment fragment) {
        super(fragment);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_image_list, null);
        ib_back = (ImageButton) view.findViewById(R.id.ib_back);
        rv_show = (RecyclerView) view.findViewById(R.id.rv_show);
        layoutManager = new GridLayoutManager(BaseApplication.getContext(), 4);

        initEvent();
        return view;
    }

    private void initEvent() {
        ib_back.setOnClickListener(this);
    }

    @Override
    public void initDate(final int id) {
        Observable.create(new Observable.OnSubscribe<List<PhotoInformation>>() {
            @Override
            public void call(Subscriber<? super List<PhotoInformation>> subscriber) {
                if (imageFind==null){
                    imageFind = new ImageFind(mContext);
                }
                switch (id){
                    case 1:
                        if (imageFind != null)
                            list = imageFind.getFilterList(ConstantValue.QQ_IMAGE_KEY);
                        break;
                    case 2:
                        if (imageFind != null)
                            list = imageFind.getFilterList(ConstantValue.WECHART_IMAGE_KEY);
                        break;
                    case 3:
                        if (imageFind != null)
                            list = imageFind.getFilterList(ConstantValue.SCREENSHOT_KEY);
                        break;
                    case 4:
                        if (imageFind != null)
                            list = imageFind.getFilterList(ConstantValue.CAMERA_KEY);
                        break;
                    case 5:
                        if (imageFind != null) {
                            list = imageFind.getGif(ConstantValue.GIF_KEY);
                        }
                    default:
                        break;
                }
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<PhotoInformation>>() {
                    @Override
                    public void call(List<PhotoInformation> photoInformationList) {
                        doAfter(photoInformationList);
                    }
                });
    }

    //点击返回键所做的操作
    @Override
    public void onClick(View v) {
        ((ThirdFragment)fragment).resume();
    }

    @Override
    public void onImageClick(PhotoInformation info) {
        if (pager==null){
            pager = new ImageShowDetail(fragment);
        }
        pager.loadImage(info);
        ((ThirdFragment)fragment).onChangeView(pager.rootView);
    }

    private void doAfter(List<PhotoInformation> photoInformationList) {
        mAdapter = new ImageAdapter(mContext, photoInformationList, layoutManager);
        //实现列表单击事件
        mAdapter.setOnImageItemClickListener(this);
        rv_show.setLayoutManager(layoutManager);
        rv_show.setAdapter(mAdapter);
    }
}
