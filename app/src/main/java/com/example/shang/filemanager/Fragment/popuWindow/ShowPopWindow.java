package com.example.shang.filemanager.Fragment.popuWindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.RelativeDateTimeFormatter;
import android.net.Uri;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.shang.filemanager.MainActivity;
import com.example.shang.filemanager.listener.ActivityForResultListener;

import java.io.File;

/**
 * Created by yaojian on 2017/11/9.
 */

public class ShowPopWindow implements ActivityForResultListener {

    private MediaItem mediaItem;
    private int x, y;//界面的大小
    private Activity mActivity;
    private WindowManager vm;
    private PopupWindow popupWindow;
    private final int REQUESTCODE = 1234;

    public ShowPopWindow(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public ShowPopWindow setMediaItem(MediaItem mediaItem) {
        this.mediaItem = mediaItem;
        return this;
    }

    public ShowPopWindow setSize(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ShowPopWindow setResource(File file) {
        mediaItem.initDate(file, x, y);
        return this;
    }

    public void show(View view) {
        if (mediaItem instanceof FilmItem) {
            askForPermission();
        } else {
            if (popupWindow == null)
                popupWindow = new PopupWindow(mediaItem.rootView, x, y);
            popupWindow.showAtLocation(view, Gravity.CENTER, 20, 20);
        }
    }

    private void askForPermission() {
        if (Settings.canDrawOverlays(mActivity)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package"+mActivity.getPackageName()));
            mActivity.startActivityForResult(intent,REQUESTCODE);
            ((MainActivity)mActivity).setmActivityForResultListener(this);
        }else{
            startView();
        }
    }

    //开启悬浮窗
    private void startView() {
        if (vm == null)
            vm = (WindowManager) mActivity.
                    getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = 2002;//表示系统级窗口
        params.format = 1;
        params.flags = 40;
        params.width = x;
        params.height = y;
        vm.addView(mediaItem.rootView, params);
    }

    public void close() {
        if (mediaItem instanceof FilmItem)
            vm.removeView(mediaItem.rootView);
        else
            popupWindow.dismiss();
        mediaItem.close();
    }

    @Override
    public void doListener(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUESTCODE){
            if(!Settings.canDrawOverlays(mActivity)){
                Toast.makeText(mActivity,"权限开启失败",Toast.LENGTH_SHORT).show();
            }else{
                startView();
            }
        }
    }
}
