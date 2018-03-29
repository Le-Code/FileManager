package com.example.shang.filemanager.utils;

import android.os.Environment;

/**
 * Created by yaojian on 2017/10/12.
 */

public class ConstantValue {
    public static final String QQ_IMAGE_KEY = ".*[Qq][Qq][_][Ii][m][a][g][e][s].*";
    public static final String WECHART_IMAGE_KEY = ".*[Ww][e][i][Xx][i][n].*";
    public static final String SCREENSHOT_KEY = ".*[Ss][c][r][e][e][n].*";
    public static final String CAMERA_KEY = ".*[D][C][I][M][/][C][a][m][e][r][a].*";
    public static final String GIF_KEY = ".*\\.[g][i][f]";

    public static final String IMAGE_MATCH = ".*\\.(jpg|JPG|png|PNG)$";
    public static final String MUSIC_MATCH = ".*\\.(mp3|MP3)$";
    public static final String FILM_MATCH = ".*\\.(mp4|MP4|avi)$";

    public static final String IMAGE = "image";
    public static final String FILM = "film";
    public static final String MUSIC = "music";

    //=======================文件传输============================================
    //wifi名称
    public static final String WIFI_AP_NAME = "fileManager";
    //wifi密码
    public static final String WIFI_AP_PWD = "1234567890";
    //Wifiap的ip地址
    public static final String AP_IP="192.168.43.1";
    //Wifiap的端口地址
    public static final int AP_PORT=2910;
    //send页面修改desc
    public static final int SEND_CHANGE_DESC = 3;
    //更新process
    public static  final  int PROCESS_CHANGED = 4;
    //get页面修改desc
    public static final int GET_CHANGE_DESC = 5;
    //发送一个Toast
    public static final int MAKE_TOAST = 9;
    //handler，file修改
    public static final int FILE_CHANGED = 10;
    //send发送完毕一个文件
    public static final int SEND_FINISHED = 11;
    //get开始接收一个文件
    public static final int GET_START = 12;

    //接收文件的路徑
    public static final String STORE_PATH =
            Environment.getExternalStorageDirectory().getPath()+"/"+"FileShared";

    //传输使用的分割符
    public static final String SPLIT= "@@@@@！！！！J1J1U1S1T1_1W1I1F1I1_1F1I1L1E1！！！！@@@@";
    //buffered大小
    public static final int GET_BUFFER_SIZE  = 1024*1024*6;
    public static final int SEND_BUFFER_SIZE  = 1024*1024;

}
