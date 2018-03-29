package com.example.shang.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shang.filemanager.R;
import com.example.shang.filemanager.entity.PhotoInformation;

import java.io.File;
import java.util.List;

/**
 * Created by yaojian on 2017/10/26.
 */

public class SixFragmentSortAdapter extends RecyclerView.Adapter<SixFragmentSortAdapter.MyView> {

    private Context mContext;
    private List<File> fileList;
    private OnFileItemLongClickListener onFileItemLongClickListener;

    public SixFragmentSortAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public void setOnFileItemLongClickListener(OnFileItemLongClickListener
                                                       onFileItemLongClickListener) {
        this.onFileItemLongClickListener = onFileItemLongClickListener;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyView(View.inflate(mContext, R.layout.item_sort_list, null));
    }

    @Override
    public void onBindViewHolder(MyView holder, int position) {
        if (fileList != null) {
            File file = fileList.get(position);
            holder.onBind(file);
        }
    }

    @Override
    public int getItemCount() {
        if (fileList != null) {
            return fileList.size();
        }
        return 0;
    }

    public class MyView extends RecyclerView.ViewHolder {

        private TextView tv_fileName;
        private TextView tv_filePath;

        public MyView(View itemView) {
            super(itemView);
            tv_fileName = (TextView) itemView.findViewById(R.id.tv_fileName);
            tv_filePath = (TextView) itemView.findViewById(R.id.tv_filePath);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onFileItemLongClickListener != null) {
                        onFileItemLongClickListener.onItemLongClick(v,getAdapterPosition());
                    }
                    return true;
                }
            });
        }

        public void onBind(File file) {
            tv_filePath.setText(file.getAbsolutePath());
            tv_fileName.setText(file.getName());
        }
    }

    public interface OnFileItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
