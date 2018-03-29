package com.example.shang.filemanager.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shang.filemanager.BaseApplication;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.adapter.FileAdapter;
import com.example.shang.filemanager.entity.FileInformation;
import com.example.shang.filemanager.utils.FileUtils;
import com.example.shang.filemanager.utils.Utils;
import com.example.shang.filemanager.view.RecycleViewDivider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Shang on 2017/7/8.
 */

public class FourFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;

    private List<FileInformation> aList = new LinkedList<FileInformation>();

    private String rootFile = FileUtils.getInstance().getBasePath();
    private List<String> recentFile = new ArrayList<>();//记忆路径实现返回

    private TextView folder_now;
    private FileAdapter adapter;

    private Context mContext = BaseApplication.getContext();

    private ImageButton ib_back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(mContext, R.layout.fragment_four,null);
        recyclerView = (RecyclerView) view.findViewById(R.id.lv);
        folder_now = (TextView) view.findViewById(R.id.folder_now);
        ib_back = (ImageButton) view.findViewById(R.id.ib_back);
        ib_back.setOnClickListener(this);
        recentFile.add(rootFile);//手动添加一个根目录
        initData();
        return view;
    }

    public void initData() {

        Observable.create(new Observable.OnSubscribe<List<FileInformation>>() {

            @Override
            public void call(Subscriber<? super List<FileInformation>> subscriber) {
                Utils utils = new Utils();
                aList = utils.getFileList(rootFile);
                subscriber.onNext(aList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<FileInformation>>() {
                    @Override
                    public void call(List<FileInformation> fileInformations) {
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(BaseApplication.getContext());
                        adapter = new FileAdapter(fileInformations);
                        adapter.addOnRecyclerViewClickListener(new FileAdapter.OnRecyclerViewClickListener() {
                            @Override
                            public void onClick(View view, int position, FileInformation data) {
                                rootFile = data.getPath().getAbsolutePath();
                                recentFile.add(rootFile);
                                initData();
                                adapter.notifyDataSetChanged();
                            }
                        });
                        folder_now.setText(rootFile);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.addItemDecoration(new RecycleViewDivider(BaseApplication.getContext(), LinearLayoutManager.HORIZONTAL, 1, R.color.black));
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_back:
                back();
                break;
            default:break;
        }
    }

    private void back() {
        //返回上一级目录
        //先判断是否已经是根目录
        if (recentFile.size()==1){
            Toast.makeText(BaseApplication.getContext(),"已经是根目录啦",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //删掉列表最后一个位置的路径
        recentFile.remove(recentFile.size() - 1);
        //获取列表最后一个位置的路径
        rootFile = recentFile.get(recentFile.size() - 1);
        initData();
        adapter.notifyDataSetChanged();
        folder_now.setText(rootFile);
    }
}
