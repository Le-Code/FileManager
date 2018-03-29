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
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shang.filemanager.BaseApplication;
import com.example.shang.filemanager.MainActivity;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.adapter.MusicRecycleViewAdapter;
import com.example.shang.filemanager.adapter.OnItemSelected;
import com.example.shang.filemanager.adapter.OnItemSelectedToOperate;
import com.example.shang.filemanager.entity.MusicInfo;
import com.example.shang.filemanager.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shang on 2017/7/24.
 * 显示音乐
 */
public class FirstFragment extends Fragment
        implements MusicRecycleViewAdapter.OnItemClickListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Context mContext = BaseApplication.getContext();
    private RecyclerView  rv_musicList;
    private List<MusicInfo> musicList;
    private ProgressBar pb_loading;
    private MusicRecycleViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private int page = 1;

    private CheckBox cb_edit;
    private LinearLayout ll_edt;
    private ImageButton ibt_delete;
    private ImageButton ibt_send;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x10:
                    pb_loading.setVisibility(View.GONE);
                    adapter = new MusicRecycleViewAdapter(mContext, musicList);
                    rv_musicList.setAdapter(adapter);
                    adapter.setClickListener(FirstFragment.this);
                    break;
                default:break;
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       View view = View.inflate(mContext,R.layout.fragment_one,null);
        rv_musicList = (RecyclerView ) view.findViewById(R.id.rv_musicList);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        cb_edit = (CheckBox) view.findViewById(R.id.ibt_edt);
        ibt_send = (ImageButton) view.findViewById(R.id.ibt_send);
        ibt_delete = (ImageButton) view.findViewById(R.id.ibt_delete);
        ll_edt = (LinearLayout) view.findViewById(R.id.ll_edt);

        cb_edit.setOnCheckedChangeListener(this);
        ibt_delete.setOnClickListener(this);
        ibt_send.setOnClickListener(this);
        initData();
        return view;

    }

    public void initData() {
        if (musicList==null) {
            musicList = new ArrayList<>();
            getMusicList();
        }else{
            mHandler.sendEmptyMessage(0x10);
            mHandler.sendEmptyMessage(0x12);
        }

        layoutManager = new LinearLayoutManager(mContext);
        rv_musicList.setLayoutManager(layoutManager);
        rv_musicList.setItemAnimator(new DefaultItemAnimator());

        //禁掉滑动删除
        /*MyItemTouchHelper myCallBack = new MyItemTouchHelper(0);
        ItemTouchHelper helper = new ItemTouchHelper(myCallBack);
        helper.attachToRecyclerView(rv_musciList);
        myCallBack.setListener(this);*/

    }

    public void getMusicList() {
        new Thread() {
            @Override
            public void run() {
                musicList = MusicUtils.getMusicList(mContext,page);
                mHandler.sendEmptyMessage(0x10);
            }
        }.start();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + musicList.get(position).getPath()), "audio/mp3");
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        showInfoDialog(musicList.get(position));
    }

    public void showInfoDialog(MusicInfo musicInfo) {
        View view = View.inflate(mContext, R.layout.dialog_music_info, null);
        Log.i("TEST", musicInfo.toString());
        ((TextView) view.findViewById(R.id.tv_title)).setText(musicInfo.getTitle());
        ((TextView) view.findViewById(R.id.tv_album)).setText(musicInfo.getAlbum());
        ((TextView) view.findViewById(R.id.tv_duration)).setText(musicInfo.getDuration() + "");
        ((TextView) view.findViewById(R.id.tv_size)).setText(musicInfo.getSize());
        ((TextView) view.findViewById(R.id.tv_artist)).setText(musicInfo.getArtist());
        ((TextView) view.findViewById(R.id.tv_path)).setText(musicInfo.getPath());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showConfirmDialog(final List<Object> filePathList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("warn")
                .setMessage("确定删除")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (Object obj:filePathList){
                            MusicUtils.deleteMusic(mContext,((MusicInfo)obj).getPath());
                            musicList.remove(obj);
                        }
                        adapter.setIsSelect(false);
                        ll_edt.setVisibility(View.GONE);
                        cb_edit.setChecked(false);
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
            case R.id.ibt_delete:
                delete();
                break;
            case R.id.ibt_send:
                send();
                break;
            default:break;
        }

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
    //处理checkBox的选择状态,并且更新界面显示编辑状态
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked){
            adapter.setIsSelect(false);
            ll_edt.setVisibility(View.GONE);
        }else{
            adapter.setIsSelect(true);
            ll_edt.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }
}
