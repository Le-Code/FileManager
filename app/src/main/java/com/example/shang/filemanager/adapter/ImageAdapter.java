package com.example.shang.filemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.entity.PhotoInformation;
import com.example.shang.filemanager.utils.ConstantValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by yaojian on 2017/10/12.
 */

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PhotoInformation> fileList;
    private List<Integer> sortList;//每个时间段的图片个数
    private int textItemCount;
    private int currentType;//用来表示非图片的type
    private Map<Integer,Integer>positionTypeMap = new TreeMap<>();
    private OnImageItemClickListener onImageItemClickListener;

    public void setOnImageItemClickListener(OnImageItemClickListener onImageItemClickListener){
        this.onImageItemClickListener = onImageItemClickListener;
    }

    public ImageAdapter(Context mContext, List<PhotoInformation> fileList, GridLayoutManager layout) {
        this.mContext = mContext;
        this.fileList = fileList;
        textItemCount = sortImageList(fileList);
        initMap();
        layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (getItemViewType(position) != textItemCount) {
                    return 4;
                } else {
                    return 1;
                }
            }
        });
    }

    private void initMap() {
        int tmp = 0;
        positionTypeMap.put(0,0);
        for (int i=1;i<textItemCount;i++){
            tmp+=sortList.get(i-1);
            positionTypeMap.put(tmp+i,i);
        }
    }

    public int sortImageList(List<PhotoInformation> fileList) {
        //因为已经排好序了
        int count = 0, numPerTime;
        sortList = new ArrayList<>();
        int size = fileList.size();
        int i = 0;
        if (size == 1) {
            sortList.add(1);
            return 1;
        }
        while (true) {
            if (i >= size - 1)
                break;
            numPerTime = 0;
            while (i <= size - 2) {
                PhotoInformation info1 = fileList.get(i);
                PhotoInformation info2 = fileList.get(i + 1);
                if (isOneDay(info1, info2)) {//是同一天
                    if (i == size - 2) {
                        numPerTime += 2;
                        sortList.add(numPerTime);
                        i += 2;
                        count++;
                        break;
                    } else {
                        numPerTime++;
                        i += 1;
                    }
                } else if (i == size - 2) {
                    numPerTime++;
                    sortList.add(numPerTime);
                    sortList.add(1);
                    count += 2;
                    i += 2;
                    break;
                } else {
                    numPerTime++;
                    sortList.add(numPerTime);
                    count++;
                    i++;
                    break;
                }
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        //type=textItemCount表示是图片，其余的均为表示分类时间的view
        if (position == 0)
            return 0;
        int tmp = 0;
        for (int i = 1; i < textItemCount; i++) {
            tmp += sortList.get(i - 1);//当前已经出现的图片的个数
            if (position == tmp + i)
                return i;
        }
        return textItemCount;
    }

    public boolean isOneDay(PhotoInformation info1, PhotoInformation info2) {
        String day1 = info1.getDate().split(" ")[0];
        String day2 = info2.getDate().split(" ")[0];
        return day1.equals(day2);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == textItemCount) {
            MyView myView = new MyView(View.inflate(mContext, R.layout.list_image_item, null));
            return myView;
        } else {
            currentType = viewType;
            SortView sortView = new SortView(View.inflate(mContext, R.layout.list_image_item2, null));
            return sortView;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = findType(position);
        if (holder instanceof MyView) {
            MyView view = (MyView) holder;
            PhotoInformation info = fileList.get(position - type - 1);
            view.onBind(info);
        } else {
            SortView view = (SortView) holder;
            view.onBind(position - type);
        }

    }

    private int findType(int position) {
        if (position==0)
            return 0;
        for (Map.Entry<Integer,Integer>entry:positionTypeMap.entrySet()){
            if (position<entry.getKey()){
                return entry.getValue()-1;
            }
        }
        return textItemCount-1;
    }

    @Override
    public int getItemCount() {
        return fileList.size() + textItemCount;
    }

    public class MyView extends RecyclerView.ViewHolder {

        private ImageView iv_image;

        public MyView(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int type = findType(getAdapterPosition());
                    PhotoInformation info = fileList.get(getAdapterPosition()-type-1);
                    if (onImageItemClickListener!=null){
                        onImageItemClickListener.onImageClick(info);
                    }
                }
            });
        }

        public void onBind(PhotoInformation info) {
            String name = info.getName();
            if (name.matches(ConstantValue.GIF_KEY)){
                Glide.with(mContext).load(info.getPath()).asGif()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(iv_image);
            }else{
                Glide.with(mContext).load(info.getPath()).fitCenter().into(iv_image);
            }
        }
    }

    public class SortView extends RecyclerView.ViewHolder {

        private TextView tv_show;

        public SortView(View itemView) {
            super(itemView);
            tv_show = (TextView) itemView.findViewById(R.id.tv_show);
        }

        public void onBind(int position) {
            if (position >= 0)
                tv_show.setText(fileList.get(position).getDate().split(" ")[0]);
        }
    }

    public interface OnImageItemClickListener{
        void onImageClick(PhotoInformation info);
    }
}
