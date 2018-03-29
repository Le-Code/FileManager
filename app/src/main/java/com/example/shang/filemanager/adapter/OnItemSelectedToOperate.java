package com.example.shang.filemanager.adapter;

import java.util.List;

/**
 * Created by yaojian on 2017/10/30.
 */

/**
 * 处理选中多个文件删除
 */
public interface OnItemSelectedToOperate {
    void doOperate(List<Object>fileToDelete);
}
