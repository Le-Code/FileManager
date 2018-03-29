package com.example.shang.filemanager.listener;

import android.content.Intent;

/**
 * Created by yaojian on 2017/11/12.
 */

public interface ActivityForResultListener {
    void doListener(int requestCode, int resultCode, Intent data);
}
