package com.example.shang.filemanager.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.shang.filemanager.MainActivity;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.adapter.DialogAdapter;

import java.io.File;
import java.util.List;

/**
 * Created by yaojian on 2017/10/25.
 */

public class ShowSendListDialog implements DialogAdapter.OnDeleteListener {

    private List<String> fileList;
    private Context mContext;
    private RecyclerView rv_sendList;
    private DialogAdapter adapter;
    private Fragment fragment;

    public ShowSendListDialog(List<String> fileList, Context mContext,Fragment fragment) {
        this.fileList = fileList;
        this.mContext = mContext;
        this.fragment = fragment;
    }

    public View initView() {
        View view = View.inflate(mContext, R.layout.dialog_show_send_list, null);
        rv_sendList = (RecyclerView) view.findViewById(R.id.rv_sendList);
        rv_sendList.setLayoutManager(new LinearLayoutManager(mContext));
        rv_sendList.setItemAnimator(new DefaultItemAnimator());
        adapter = new DialogAdapter(fileList, mContext);
        adapter.setOnDeleteListener(this);
        rv_sendList.setAdapter(adapter);
        return view;
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity())
                .setView(initView())
                .setPositiveButton("确定",null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onDelete(int position) {
        ((MainActivity)fragment.getActivity()).removeSendFilePathListItem(fileList.get(position));
        fileList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
