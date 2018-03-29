package com.example.shang.filemanager.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.PatternPathMotion;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shang.filemanager.BaseApplication;
import com.example.shang.filemanager.MainActivity;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.adapter.FilmRecycleViewAdapter;
import com.example.shang.filemanager.adapter.OnItemSelected;
import com.example.shang.filemanager.adapter.OnItemSelectedToOperate;
import com.example.shang.filemanager.entity.FilmInfo;
import com.example.shang.filemanager.music.MyItemTouchHelper;
import com.example.shang.filemanager.utils.FilmUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shang on 2017/7/24.
 */
public class SecondFragment extends Fragment implements
        FilmRecycleViewAdapter.OnItemClickListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private RecyclerView rv_film;
    private LinearLayout ll_edt_film;
    private ImageButton ibt_delete_film;
    private ImageButton ibt_send_film;
    private CheckBox cb_edt_film;

    private Context mContext = BaseApplication.getContext();
    private List<FilmInfo>filmList;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            adapter = new FilmRecycleViewAdapter(mContext,filmList);
            adapter.setClickListener(SecondFragment.this);
            /*MyItemTouchHelper callBack = new MyItemTouchHelper(1);
            ItemTouchHelper helper = new ItemTouchHelper(callBack);
            callBack.setListener(SecondFragment.this);
            helper.attachToRecyclerView(rv_film);*/
            rv_film.setAdapter(adapter);
        }
    };
    private FilmRecycleViewAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(mContext,R.layout.fragment_two,null);

        initView(view);
        initEvent();
        rv_film.setLayoutManager(new LinearLayoutManager(mContext));
        rv_film.setItemAnimator(new DefaultItemAnimator());

        if (filmList==null){
            initData();
        }else{
            mHandler.sendEmptyMessage(0x123);
        }
        return view;
    }

    private void initEvent() {
        ibt_delete_film.setOnClickListener(this);
        ibt_send_film.setOnClickListener(this);
        cb_edt_film.setOnCheckedChangeListener(this);
    }

    private void initView(View view) {
        rv_film = (RecyclerView) view.findViewById(R.id.rv_film);
        ll_edt_film = (LinearLayout) view.findViewById(R.id.ll_edt_film);
        ibt_delete_film = (ImageButton) view.findViewById(R.id.ibt_delete_film);
        ibt_send_film = (ImageButton) view.findViewById(R.id.ibt_send_film);
        cb_edt_film = (CheckBox) view.findViewById(R.id.ibt_edt_film);
    }

    public void initData() {
        mContext = BaseApplication.getContext();
        filmList = new ArrayList<>();
        GetFilmData();
    }

    private void GetFilmData() {
        new Thread(){
            @Override
            public void run() {
                filmList = FilmUtils.getFilmList(mContext);
                mHandler.sendEmptyMessage(0x123);
            }
        }.start();
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filmList.get(position).getPath()), "video/mp4");
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        showInfoDialog(filmList.get(position));
    }

    public void showInfoDialog(FilmInfo filmInfoInfo) {
        View view = View.inflate(mContext, R.layout.dialog_film_info, null);
        ((TextView) view.findViewById(R.id.tv_title)).setText(filmInfoInfo.getTitle());
        ((TextView) view.findViewById(R.id.tv_size)).setText(filmInfoInfo.getSize() + "");
        ((TextView) view.findViewById(R.id.tv_path)).setText(filmInfoInfo.getPath());
        ((TextView) view.findViewById(R.id.tv_date)).setText(filmInfoInfo.getDate());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showConfirmDialog(final List<Object> filePathList){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("warn")
                .setMessage("确定删除")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (Object obj:filePathList){
                            FilmUtils.deleteFilm(mContext,((FilmInfo)obj).getPath());
                            filmList.remove(obj);
                        }
                        adapter.setIsSelect(false);
                        ll_edt_film.setVisibility(View.GONE);
                        cb_edt_film.setChecked(false);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibt_delete_film:
                delete();
                break;
            case R.id.ibt_send_film:
                send();
                break;
            default:break;
        }
    }

    private void delete() {
        adapter.setOnItemSelectedToOperate(new OnItemSelectedToOperate() {
            @Override
            public void doOperate(List<Object> fileToDelete) {
                if (fileToDelete==null){
                    Toast.makeText(mContext,"未选择文件",Toast.LENGTH_SHORT).show();
                    return;
                }
                showConfirmDialog(fileToDelete);
            }
        });
    }

    private void send() {
        adapter.setOnItemSelected(new OnItemSelected() {
            @Override
            public void itemSelect(List<String> filePathList) {
                if (filePathList==null){
                    Toast.makeText(mContext,"未选择文件",Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(mContext,"添加"+filePathList.size()+"条内容进待发送队列",
                        Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).addSendFilePathListItem(filePathList);
            }
        });
    }

    //更新界面，显示编辑状态
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked){
            adapter.setIsSelect(false);
            ll_edt_film.setVisibility(View.GONE);
        }else{
            adapter.setIsSelect(true);
            ll_edt_film.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    //界面销毁时做的工作
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cb_edt_film.setChecked(false);
        ll_edt_film.setVisibility(View.GONE);
    }
}
