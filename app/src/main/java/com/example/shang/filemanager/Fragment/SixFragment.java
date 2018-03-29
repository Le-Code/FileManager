package com.example.shang.filemanager.Fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shang.filemanager.BaseApplication;
import com.example.shang.filemanager.Fragment.popuWindow.FilmItem;
import com.example.shang.filemanager.Fragment.popuWindow.MediaItem;
import com.example.shang.filemanager.Fragment.popuWindow.MusicItem;
import com.example.shang.filemanager.Fragment.popuWindow.PhotoItem;
import com.example.shang.filemanager.Fragment.popuWindow.ShowPopWindow;
import com.example.shang.filemanager.MainActivity;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.adapter.SixFragmentSortAdapter;
import com.example.shang.filemanager.listener.MyTouchListener;
import com.example.shang.filemanager.utils.ConstantValue;
import com.example.shang.filemanager.utils.FastBlurUtility;
import com.example.shang.filemanager.utils.FileUtils2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Shang on 2017/7/24.
 */
public class SixFragment extends Fragment implements
        SixFragmentSortAdapter.OnFileItemLongClickListener, MyTouchListener, View.OnClickListener {

    private int[] colors = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFF00FFFF};//每一个模块的颜色标记
    private String[] labels = {"音乐", "视频", "图片", "其他"};
    private RecyclerView rv_showSortList;
    private PieChartView pieView;

    private ImageButton ibt_delete_six;
    private ImageButton ibt_exit_six;

    private PieChartData mPieChartData;
    private TreeSet<File> filmSet;
    private TreeSet<File> musicSet;
    private TreeSet<File> imageSet;
    private long[] itemSize = new long[10];
    private long memoryAvailable;
    private long memoryTotal;

    private SixFragmentSortAdapter mAdapter;

    private Map<String, TreeSet<File>> mMap;

    private ShowPopWindow mPopWindow;

    private List<File> fileList = new ArrayList<>();//暂时存储需要展示的列表

    private boolean longClickFlag = false;//是否处于长按标志
    private boolean isSure = false;//判断虚化是否处于固定位置

    private LinearLayout linearLayout;

    private Bitmap bitmap;//虚化的背景
    private int startY = 0;//记录点击起始位置
    private LinearLayout show_op_layout;//向上滑动出来的一些操作

    private int dencity = 0;

    private MediaItem mediaItem;

    private File selectFile;//选中的file
    private int selectPos;//选中的位置
    private String selectType;//选中的类型


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = View.inflate(BaseApplication.getContext(), R.layout.fragment_six, null);
        initView(view);
        initPieView();
        if (mMap != null) {
            loadPieView();
        } else {
            initDate();
        }

        return view;
    }

    private void initDate() {
        Observable.create(new Observable.OnSubscribe<Map<String, TreeSet<File>>>() {
            @Override
            public void call(Subscriber<? super Map<String, TreeSet<File>>> subscriber) {
                subscriber.onNext(FileUtils2.getFileSortConcurrent());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, TreeSet<File>>>() {
                    @Override
                    public void call(Map<String, TreeSet<File>> map) {
                        mMap = map;
                        doAfter();
                    }
                });
    }

    //获取数据以后后续的操作
    private void doAfter() {
        filmSet = mMap.get(ConstantValue.FILM);
        musicSet = mMap.get(ConstantValue.MUSIC);
        imageSet = mMap.get(ConstantValue.IMAGE);
        itemSize[0] = totalSize(musicSet);
        itemSize[1] = totalSize(filmSet);
        itemSize[2] = totalSize(imageSet);
        //其他容量
        itemSize[3] = memoryTotal - memoryAvailable - itemSize[0] - itemSize[1] - itemSize[2];
        loadPieView();
    }

    //加载饼状图
    private void loadPieView() {
        List<SliceValue> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            SliceValue value = new SliceValue(itemSize[i], colors[i]);
            value.setLabel(labels[i]);
            list.add(value);
        }
        mPieChartData.setValues(list);
        pieView.setPieChartData(mPieChartData);
        pieView.setOnValueTouchListener(touchListener);
    }

    public long totalSize(TreeSet<File> treeSet) {
        long sum = 0;
        for (File file : treeSet) {
            sum += file.length();
        }
        return sum;
    }

    private void initView(View view) {
        rv_showSortList = (RecyclerView) view.findViewById(R.id.rv_showSortList);
        pieView = (PieChartView) view.findViewById(R.id.pieView);
        linearLayout = (LinearLayout) view.findViewById(R.id.six_layout);
        show_op_layout = (LinearLayout) view.findViewById(R.id.show_op_layout);

        ibt_delete_six = (ImageButton) view.findViewById(R.id.ibt_delete_six);
        ibt_exit_six = (ImageButton) view.findViewById(R.id.ibt_exit_six);
        ibt_exit_six.setOnClickListener(this);
        ibt_delete_six.setOnClickListener(this);

        rv_showSortList.setLayoutManager(new LinearLayoutManager(BaseApplication.getContext()));
        rv_showSortList.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new SixFragmentSortAdapter(BaseApplication.getContext());
        rv_showSortList.setAdapter(mAdapter);
        mAdapter.setOnFileItemLongClickListener(this);
        //监听手势动作
        ((MainActivity) getActivity()).setMyTouchListener(this);
        //计算屏幕密度
        if (dencity==0)
            dencity = (int) getScreenSizeOfDevice();
    }

    private void initPieView() {
        mPieChartData = new PieChartData();
        mPieChartData.setHasCenterCircle(true);//中间矩形显示

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        memoryAvailable = getAvailSize(path);
        memoryTotal = getTotalSize(path);
        mPieChartData.setCenterText1(Formatter.formatFileSize(BaseApplication.getContext(),
                memoryTotal - memoryAvailable) + "/" +
                Formatter.formatFileSize(BaseApplication.getContext(), memoryTotal));//中间的字
        mPieChartData.setCenterText1FontSize(10);
        mPieChartData.setCenterText1Color(Color.BLACK);
        mPieChartData.setCenterCircleScale(0.5f);//中间圆形的大小
        pieView.setValueSelectionEnabled(true);//选中变大
    }

    //设立饼状图的监听事件
    private PieChartOnValueSelectListener touchListener = new PieChartOnValueSelectListener() {
        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            String name = String.valueOf(value.getLabelAsChars());
            switch (name) {
                case "音乐":
                    fileList.clear();
                    selectType = ConstantValue.MUSIC;
                    mediaItem = new MusicItem();
                    fileList.addAll(musicSet);
                    mAdapter.setFileList(fileList);
                    break;
                case "视频":
                    fileList.clear();
                    selectType = ConstantValue.FILM;
                    mediaItem = new FilmItem();
                    fileList.addAll(filmSet);
                    mAdapter.setFileList(fileList);
                    break;
                case "图片":
                    fileList.clear();
                    selectType = ConstantValue.IMAGE;
                    mediaItem = new PhotoItem();
                    fileList.addAll(imageSet);
                    mAdapter.setFileList(fileList);
                    break;
                default:
                    fileList.clear();
                    mAdapter.setFileList(fileList);
                    break;
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onValueDeselected() {

        }
    };

    //int类型的大小已经不够作为容量的类型，int能够作为2G的存储类型
    private long getAvailSize(String path) {
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(path);

        long blockSize = stat.getBlockSize();   // 获得一个扇区的大小

        long availableBlocks = stat.getAvailableBlocks();   // 获得可用的扇区数量

        return availableBlocks * blockSize;
    }

    //获得总存储容量
    private long getTotalSize(String path) {
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(path);

        long blockSize = stat.getBlockSize();   // 获得一个扇区的大小

        long totalBlocks = stat.getBlockCount();    // 获得扇区的总数

        return totalBlocks * blockSize;
    }

    @Override
    public void onItemLongClick(View view, int position) {
        longClickFlag = true;
        selectFile = fileList.get(position);
        selectPos = position;
        mPopWindow = new ShowPopWindow(getActivity());
        if (bitmap == null)
            bitmap = FastBlurUtility.getBlurBackgroundDrawer(getActivity());
        linearLayout.setBackground(new BitmapDrawable(bitmap));
        ShowOrHideView(View.INVISIBLE);
        mPopWindow.setMediaItem(mediaItem)
                .setSize(getResources().getDisplayMetrics().widthPixels*3/4,
                        getResources().getDisplayMetrics().widthPixels*3/4)
                .setResource(selectFile)
                .show(view);
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        if (!longClickFlag) {
            startY = (int) event.getY();
        }
        if (longClickFlag) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (!isSure){//没有固定位置松手则关闭
                        closeBlur();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int endY = (int) event.getY();
                    if (startY>endY) { //上划
                        int dy = startY - endY;//上移多少位置
                        if (dy<=40){
                            setMargin(-40+dy);
                            isSure = false;
                        }else{
                            isSure = true;
                            setMargin(40);
                        }
                    }else{
                        isSure = false;
                        setMargin(0-50*dencity);
                    }
                    break;
            }
        }
        return false;
    }

    public void closeBlur(){
        mPopWindow.close();
        linearLayout.setBackgroundColor(Color.WHITE);
        ShowOrHideView(View.VISIBLE);
        longClickFlag = false;
        isSure = false;
        setMargin(0-50*dencity);
    }

    //设置底部的margin值
    public void setMargin(int x){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                show_op_layout.getLayoutParams();
        params.bottomMargin = x;
        show_op_layout.setLayoutParams(params);
    }

    /**
     * 计算屏幕密度
     */
    private double getScreenSizeOfDevice() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        double x = Math.pow(width,2);
        double y = Math.pow(height,2);
        double diagonal = Math.sqrt(x+y);

        int dens=dm.densityDpi;
        return (diagonal/(double)dens);
    }

    //解决背景虚化问题
    public void ShowOrHideView(int flag) {
        rv_showSortList.setVisibility(flag);
        pieView.setVisibility(flag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibt_delete_six:
                deleteFile();
                break;
            case R.id.ibt_exit_six:
                closeBlur();
                break;
        }
    }

    private void deleteFile() {
        if (selectFile.delete()){
            closeBlur();
            //0.2秒的等待只为看你一眼
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mMap.get(selectType).remove(selectFile);
            doAfter();
            initPieView();
            fileList.remove(selectPos);
            mAdapter.notifyItemRemoved(selectPos);
            //刷新饼状图
        }else{
            Toast.makeText(BaseApplication.getContext(),"删除失败",Toast.LENGTH_SHORT).show();
        }
    }
}
