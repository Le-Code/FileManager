package com.example.filetransport.activity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filetransport.R;
import com.example.filetransport.bean.FileBeanSimple;
import com.example.filetransport.conn.ConnManager;
import com.example.filetransport.conn.WifiApControl;
import com.example.filetransport.sock.SocketManager;
import com.example.filetransport.transport.FileTransport;
import com.example.filetransport.utils.ConstantValues;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by yaojian on 2017/10/23.
 */

public class SendActivity extends AppCompatActivity implements View.OnClickListener {

    private Socket socket = null;
    private Thread apOpenThread;
    private WifiManager wifiManager;
    private boolean exit = false;

    private TextView tv_showState;
    private Button btn_sendFile;
    private TextView tv_showProgress;

    private boolean isRunning = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstantValues.SEND_CHANGE_DESC:
                    tv_showState.setText((String) msg.obj);
                    break;
                case ConstantValues.PROCESS_CHANGED:
                    tv_showProgress.setText((String)msg.obj);
                case ConstantValues.SEND_FINISHED:
                    isRunning = false;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        initView();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        apOpenThread = new Thread(new TestApOpenThread());
        apOpenThread.start();
    }

    @Override
    public void onClick(View view) {
        FileBeanSimple file = new FileBeanSimple(new File(
                Environment.getExternalStorageDirectory().getPath()+"/Movies/movie.mp4"
        ));
        if (!isRunning){//發送文件
            sendFile(file);
        }else{
            Toast.makeText(SendActivity.this,"现在已有文件进行发送,请稍后再试",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 測試開啓熱點
     * 檢測socket鏈接
     */
    private class TestApOpenThread implements Runnable {

        @Override
        public void run() {
            while (WifiApControl.getWifiApState(wifiManager).equals(
                    WifiApControl.WIFI_AP_STATE.WIFI_AP_STATE_ENABLED
            )){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ConnManager.sendHandler("成功开启热点，等待接收端连接"
                    ,mHandler,ConstantValues.SEND_CHANGE_DESC);
            while (!exit){
                //開啓socket監聽
                try {
                    socket = SocketManager.getInstance().listenSocket();
                    ConnManager.sendHandler("成功與接受端連接，可以發送文件",mHandler,
                            ConstantValues.SEND_CHANGE_DESC);
                } catch (IOException e) {
                    e.printStackTrace();
                    ConnManager.sendHandler("鏈接失敗，等待重連",mHandler,
                            ConstantValues.SEND_CHANGE_DESC);
                }
            }
        }
    }

    private void initView() {
        tv_showState = (TextView) findViewById(R.id.tv_showState);
        btn_sendFile = (Button) findViewById(R.id.btn_sendFile);
        tv_showProgress = (TextView) findViewById(R.id.tv_showProgress);
        btn_sendFile.setOnClickListener(this);
    }

    public void sendFile(final FileBeanSimple file){
        if (socket!=null){
            isRunning = true;
            new Thread(){
                @Override
                public void run() {
                    FileTransport.sendFile(file,socket,mHandler);
                }
            }.start();
        }else{
            Toast.makeText(this,"接收端尚未链接,请等待接受端链接后再次发送",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exit = true;
        if (socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        WifiApControl.closeWifiAp(wifiManager);
    }
}
