package com.example.shang.filemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shang.filemanager.Fragment.FragmentFactory;
import com.example.shang.filemanager.listener.ActivityForResultListener;
import com.example.shang.filemanager.listener.MyTouchListener;
import com.nineoldandroids.view.ViewHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ViewPager vp;
    private TextView tv_page_title;

    private MyTouchListener myTouchListener;//将监听传递给fragment
    private ActivityForResultListener mActivityForResultListener;//响应intent返回的事件

    private float density;
    private ScaleGestureDetector mScaleGestureDetector = null;

    private List<String> pageTitleList = Arrays.asList("音乐", "视频", "图片", "文件", "分享", "可视化");

    private Set<String> sendFilePathList = new HashSet<>();

    private final int EXTERNAL_STORAGE_REQ_CODE = 10;//访问存储权限回调

    public void addSendFilePathListItem(List<String> paths) {
        sendFilePathList.addAll(paths);
        Log.i("tag", sendFilePathList.toString());
    }

    public Set<String> getSendFilePathList() {
        return sendFilePathList;
    }

    public void removeSendFilePathListItem(String path) {
        sendFilePathList.remove(path);
        Log.i("tag", sendFilePathList.toString());
    }

    public void setMyTouchListener(MyTouchListener myTouchListener) {
        this.myTouchListener = myTouchListener;
    }

    public void setmActivityForResultListener(ActivityForResultListener mActivityForResultListener) {
        this.mActivityForResultListener = mActivityForResultListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //解决FileUriExposedException。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        requestPermission();
        setContentView(R.layout.activity_main);
        density = getResources().getDisplayMetrics().density;
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureListener());
        vp = (ViewPager) findViewById(R.id.vp);
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);

        initLayoutParam();

        vp.setOffscreenPageLimit(1);
        vp.setPageTransformer(true, new FlipPagerTransformer());
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_page_title.setText(pageTitleList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //申请访问存储的权限
    public void requestPermission() {
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "please give me the permission", Toast.LENGTH_SHORT).show();
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQ_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQ_CODE: {
                // 如果请求被拒绝，那么通常grantResults数组为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请成功，进行相应操作
                    Toast.makeText(this, "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    //申请失败，可以继续向用户解释。
                    Toast.makeText(this, "申请失败", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void initLayoutParam() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        ViewGroup.LayoutParams params = vp.getLayoutParams();
        params.width = width * 3 / 4;
        params.height = height * 3 / 4;
        vp.setLayoutParams(params);
    }

    class FlipPagerTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            if (position <= 0 && position >= -1) {
                page.setPivotX(page.getMeasuredWidth());
            } else if (position <= 1 && position >= -1) {
                page.setPivotX(0);
            }
            page.setPivotY(page.getMeasuredHeight() * 0.5f);
            Log.i("tag", density + "");
            if (density <= 1.5f) {
                page.setRotationY(position * 90f);
            } else if (1.5f < density && density <= 2.0f) {
                page.setRotationY(position * 75f);
            } else if (2.0f < density && density <= 2.5f) {
                page.setRotationY(position * 60f);
            } else if (2.5f < density && density <= 3.0f) {
                page.setRotationY(position * 45f);
            } else if (3.0f < density && density <= 3.5f) {
                page.setRotationY(position * 30f);
            } else if (3.5f < position) {
                page.setRotationY(position * 15f);
            }
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = FragmentFactory.getFragment(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return 6;
        }
    }

    public class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

        private float scale;
        private float preScale = 1;// 默认前一次缩放比例为1

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float previousSpan = detector.getPreviousSpan();
            float currentSpan = detector.getCurrentSpan();
            if (currentSpan < previousSpan) {
                // 缩小
                // scale = preScale-detector.getScaleFactor()/3;
                scale = preScale - (previousSpan - currentSpan) / 1000;
                if (scale < 0) {
                    scale = 0;
                }
            } else {
                // 放大
                // scale = preScale+detector.getScaleFactor()/3;
                scale = preScale + (currentSpan - previousSpan) / 1000;
            }

            // 缩放view
            ViewHelper.setScaleX(vp, scale);// x方向上缩小
            ViewHelper.setScaleY(vp, scale);// y方向上缩小
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // 一定要返回true才会进入onScale()这个函数
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            preScale = scale;//记录本次缩放比例
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mActivityForResultListener != null)
            mActivityForResultListener.doListener(requestCode, resultCode, data);
    }

    //处理收拾操作
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (myTouchListener != null) {
            myTouchListener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 返回给ScaleGestureDetector来处理
        return mScaleGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.ap2, R.anim.ap1);
    }
}
