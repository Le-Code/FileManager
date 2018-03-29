package com.example.shang.filemanager.Fragment.imagePager;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.shang.filemanager.Fragment.ThirdFragment;
import com.example.shang.filemanager.R;

/**
 * Created by yaojian on 2017/10/20.
 */

public class ImageSort extends BasePager implements View.OnClickListener {


    private RelativeLayout rl_qq;
    private RelativeLayout rl_weChart;
    private RelativeLayout rl_ScreenShots;
    private RelativeLayout rl_camera;
    private RelativeLayout rl_gif;

    private ImageShowList mImageShowList;

    public ImageSort(Fragment fragment) {
        super(fragment);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.image_sort, null);
        rl_qq = (RelativeLayout) view.findViewById(R.id.rl_qq);
        rl_weChart = (RelativeLayout) view.findViewById(R.id.rl_weChart);
        rl_camera = (RelativeLayout) view.findViewById(R.id.rl_camera);
        rl_ScreenShots = (RelativeLayout) view.findViewById(R.id.rl_ScreenShots);
        rl_gif = (RelativeLayout) view.findViewById(R.id.rl_gif);
        initEvent();
        return view;
    }

    private void initEvent() {
        rl_ScreenShots.setOnClickListener(this);
        rl_camera.setOnClickListener(this);
        rl_weChart.setOnClickListener(this);
        rl_qq.setOnClickListener(this);
        rl_gif.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mImageShowList == null) {
            mImageShowList = new ImageShowList(fragment);
        }
        int id = 0;
        switch (v.getId()) {
            case R.id.rl_camera:
                id = 4;
                break;
            case R.id.rl_qq:
                id = 1;
                break;
            case R.id.rl_ScreenShots:
                id = 3;
                break;
            case R.id.rl_weChart:
                id = 2;
                break;
            case R.id.rl_gif:
                id = 5;
                break;
            default:
                break;
        }
        if (id != 0) {
            mImageShowList.initDate(id);
            ((ThirdFragment) fragment).onChangeView(mImageShowList.rootView);

        }
    }
}
