package com.example.shang.filemanager.Fragment.popuWindow;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.shang.filemanager.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by yaojian on 2017/11/9.
 */

public class MusicItem extends MediaItem {

    private ImageView iv_music_player;
    private TextView tv_end_time;
    private TextView tv_music_name;
    private SeekBar seekBar_music;
    private MediaPlayer mPlayer;
    private PlayThread mPlayThread;
    private boolean isExit = false;
    public String name;
    private int currentPos = 0;//记录当前播放的位置

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_end_time.setText((String)msg.obj);
            seekBar_music.setProgress(currentPos+1000);
            currentPos+=1000;
        }
    };

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.popu_music_item,null);
        iv_music_player = (ImageView) view.findViewById(R.id.iv_music_player);
        tv_end_time = (TextView) view.findViewById(R.id.tv_end_time);
        tv_music_name = (TextView) view.findViewById(R.id.tv_music_name);
        seekBar_music = (SeekBar) view.findViewById(R.id.seekBar_music);
        return view;
    }

    @Override
    public void initDate(File file, int x, int y) {
        tv_music_name.setSelected(true);
        tv_music_name.setText(file.getName().replaceAll("\\..*$",""));
        //播放音乐
        playMusic(file.getAbsolutePath());
        //设置图片旋转
        iv_music_player.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.rotate_music_player));
        //实时显示时间
        isExit = false;
        seekBar_music.setMax(mPlayer.getDuration());
        mPlayThread = new PlayThread();
        mPlayThread.start();
    }

    private void playMusic(String path) {
        try {
            mPlayer = new MediaPlayer();
            //装载指定的声音文件
            mPlayer.setDataSource(path);
            //准备声音
            mPlayer.prepare();
            //播放
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        //离开标志
        isExit = true;
        mPlayer.stop();
        //释放资源
        mPlayer.release();
        currentPos = 0;
    }

    public class PlayThread extends Thread{
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        int time = mPlayer.getDuration();
        String showText = format.format(time);
        Message msg;
        @Override
        public void run() {
            do{
                msg = Message.obtain();
                msg.obj = showText;
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time-=1000;
                showText = format.format(time);
            }while (!showText.equals("00:00")&&!isExit);
        }
    }
}
