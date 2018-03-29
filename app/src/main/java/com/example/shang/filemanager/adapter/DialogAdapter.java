package com.example.shang.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.shang.filemanager.R;
import com.example.shang.filemanager.entity.FileBeanSimple;

import java.io.File;
import java.util.List;

/**
 * Created by yaojian on 2017/10/25.
 */

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.MyView> {

    public interface OnDeleteListener{
        void onDelete(int position);
    }

    private List<String> fileList;
    private Context mContext;
    private OnDeleteListener onDeleteListener;

    public DialogAdapter(List<String>fileList,Context mContext){
        this.fileList = fileList;
        this.mContext = mContext;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener){
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyView(View.inflate(mContext,R.layout.dialog_send_item,null));
    }

    @Override
    public void onBindViewHolder(MyView holder, int position) {
        FileBeanSimple file = new FileBeanSimple(new File(fileList.get(position)));
        holder.onBindView(file);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        private TextView tv_showFileName;
        private TextView tv_showFileSize;
        private ImageButton ibt_delete_sendList;

        public MyView(View itemView) {
            super(itemView);
            tv_showFileName = (TextView) itemView.findViewById(R.id.tv_showFileName);
            tv_showFileSize = (TextView) itemView.findViewById(R.id.tv_showFileSize);
            ibt_delete_sendList = (ImageButton) itemView.findViewById(R.id.ibt_delete_sendList);
            ibt_delete_sendList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("test",getAdapterPosition()+",position");
                    if (onDeleteListener!=null)
                        onDeleteListener.onDelete(getAdapterPosition());
                }
            });
        }

        public void onBindView(FileBeanSimple file){
            tv_showFileSize.setText(file.getFileSize());
            tv_showFileName.setText(file.getFileName());
        }
    }
}
