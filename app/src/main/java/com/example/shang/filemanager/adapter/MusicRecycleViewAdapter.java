package com.example.shang.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shang.filemanager.R;
import com.example.shang.filemanager.entity.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaojian on 2017/7/31.
 */

public class MusicRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    //处理点击事件
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }


    private OnItemClickListener clickListener;

    private Context mContext;
    private List<MusicInfo> musicInfoList;
    private List<String> filePathList = new ArrayList<>();//发送
    private List<Object> filePathToDelete = new ArrayList<>();//删除列表


    private boolean isSelect = false;//默认不出现checkBox

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        if (filePathList.size() > 0) {
            onItemSelected.itemSelect(filePathList);
        }
    }

    public void setOnItemSelectedToOperate(OnItemSelectedToOperate onItemSelectedToOperate) {
        if (filePathToDelete.size() > 0)
            onItemSelectedToOperate.doOperate(filePathToDelete);
    }

    public MusicRecycleViewAdapter(Context mContext, List<MusicInfo> musicInfoList) {
        this.mContext = mContext;
        this.musicInfoList = musicInfoList;
    }


    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(mContext, R.layout.item_music_list, null);
        MyView myView = new MyView(itemView);
        return myView;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyView myView = (MyView) holder;
        myView.onBind(position);
        myView.cbIsShow();
    }

    @Override
    public int getItemCount() {
        return musicInfoList.size();
    }


    public class MyView extends RecyclerView.ViewHolder {

        public ImageView iv_music;
        public TextView tv_title;
        public TextView tv_album;
        public CheckBox cb_selected;

        public MyView(View itemView) {
            super(itemView);
            iv_music = (ImageView) itemView.findViewById(R.id.iv_music);
            tv_album = (TextView) itemView.findViewById(R.id.tv_album);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            cb_selected = (CheckBox) itemView.findViewById(R.id.cb_select);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSelect) {//处于编辑状态
                        if (cb_selected.isChecked()) {//已经处于选中状态，再次点击则为取消
                            filePathList.remove(musicInfoList.get(getAdapterPosition()).getPath());
                            filePathToDelete.remove(musicInfoList.get(getAdapterPosition()));
                            cb_selected.setChecked(false);
                        } else {//当前不是选中状态
                            filePathList.add(musicInfoList.get(getAdapterPosition()).getPath());
                            filePathToDelete.add(musicInfoList.get(getAdapterPosition()));
                            cb_selected.setChecked(true);
                        }
                    } else {
                        if (clickListener != null)
                            clickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (clickListener != null)
                        clickListener.onItemLongClick(view, getAdapterPosition());
                    return true;
                }
            });
        }

        public void cbIsShow() {
            if (isSelect) {
                cb_selected.setVisibility(View.VISIBLE);
            } else {
                cb_selected.setVisibility(View.INVISIBLE);
                filePathList.clear();
                filePathToDelete.clear();
            }
        }

        public void onBind(int position) {
            MusicInfo info = musicInfoList.get(position);
            tv_album.setText(info.getArtist());
            tv_title.setText(info.getTitle());
            iv_music.setImageDrawable(info.getBitmapDrawable());
        }
    }
}
