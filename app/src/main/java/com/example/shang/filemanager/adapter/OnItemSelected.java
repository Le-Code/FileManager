package com.example.shang.filemanager.adapter;

/**
 * Created by yaojian on 2017/10/25.
 */

import java.util.List;

//进行选择回调
public interface OnItemSelected{
    void itemSelect(List<String> filePathList);
}