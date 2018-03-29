package com.example.shang.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shang.filemanager.R;
import com.example.shang.filemanager.entity.FilmInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaojian on 2017/7/31.
 */

public class FilmRecycleViewAdapter extends RecyclerView.Adapter<FilmRecycleViewAdapter.MyView> {

    //处理点击事件
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }



    private OnItemClickListener clickListener;
    private Context mContext;
    private List<FilmInfo> filmInfoList;
    private boolean isSelect = false;//默认不开启
    private List<String>filePathList = new ArrayList<>();//发送
    private List<Object>filePathToDelete = new ArrayList<>();

    public void setIsSelect(boolean isSelect){
        this.isSelect = isSelect;
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public FilmRecycleViewAdapter(Context mContext, List<FilmInfo> filmInfoList){
        this.mContext = mContext;
        this.filmInfoList = filmInfoList;
    }

    public void setOnItemSelected(OnItemSelected onItemSelected){
        if (filePathList.size()>0)
            onItemSelected.itemSelect(filePathList);
    }

    public void setOnItemSelectedToOperate(OnItemSelectedToOperate onItemSelectedToOperate) {
        if (filePathToDelete.size() > 0)
            onItemSelectedToOperate.doOperate(filePathToDelete);
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        MyView holder = new MyView(View.inflate(mContext, R.layout.item_film_list,null));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyView holder, int position) {
        holder.onBind(position);
        holder.cbIsShow();
    }

    @Override
    public int getItemCount() {
        return filmInfoList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        public ImageView iv_film;
        public TextView tv_title;
        private CheckBox cb_select_film;

        public MyView(View itemView) {
            super(itemView);
            iv_film = (ImageView) itemView.findViewById(R.id.iv_film);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            cb_select_film = (CheckBox) itemView.findViewById(R.id.cb_select_film);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSelect){//处于编辑状态
                        if (cb_select_film.isChecked()){//已经处于选中状态，再次点击则为取消
                            filePathList.remove(filmInfoList.get(getAdapterPosition()).getPath());
                            filePathToDelete.remove(filmInfoList.get(getAdapterPosition()));
                            cb_select_film.setChecked(false);
                        }else{//当前不是选中状态
                            filePathList.add(filmInfoList.get(getAdapterPosition()).getPath());
                            filePathToDelete.add(filmInfoList.get(getAdapterPosition()));
                            cb_select_film.setChecked(true);
                        }
                    }else{
                        if (clickListener!=null)
                            clickListener.onItemClick(view,getAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (clickListener!=null)
                        clickListener.onItemLongClick(view,getAdapterPosition());
                    return true;
                }
            });
        }

        //是否打开checkBox
        public void cbIsShow(){
            if (isSelect){
                cb_select_film.setVisibility(View.VISIBLE);
            }else{
                cb_select_film.setVisibility(View.INVISIBLE);
                filePathList.clear();
                filePathToDelete.clear();
            }
        }

        public void onBind(int position){
            Log.d("TEST", "onBind: "+filmInfoList.size()+"");
            String title = filmInfoList.get(position).getTitle();
           tv_title.setText(title);
        }
    }
}
