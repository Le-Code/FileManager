package com.example.shang.filemanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shang.filemanager.BaseApplication;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.entity.FileInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shang on 2017/7/26.
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyFileViewHolder> implements View.OnClickListener {

    private RecyclerView recyclerView;

    private List<FileInformation> list = new ArrayList<FileInformation>();

    public OnRecyclerViewClickListener listener;

    public interface OnRecyclerViewClickListener{
        void onClick(View view, int position, FileInformation data);
    }

    public void addOnRecyclerViewClickListener(OnRecyclerViewClickListener listener) {
        this.listener = listener;
    }

    public FileAdapter(List<FileInformation> list) {
        this.list = list;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public MyFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(BaseApplication.getContext(), R.layout.file_item, null);
        view.setOnClickListener(this);
        return new MyFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyFileViewHolder holder, int position) {
        holder.icon.setImageResource(list.get(position).getIconId());
        holder.name.setText(list.get(position).getName());
        holder.size.setText(list.get(position).getSize());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            int position = recyclerView.getChildAdapterPosition(v);
            FileInformation information = list.get(position);
            listener.onClick(v, position, information);
        }
    }

    public class MyFileViewHolder extends RecyclerView.ViewHolder{

        private final ImageView icon;
        private final TextView name;
        private final TextView size;

        public MyFileViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
            size = (TextView) itemView.findViewById(R.id.size);
        }
    }
}
