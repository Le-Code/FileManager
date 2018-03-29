package com.example.filetransport.activity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.filetransport.R;
import com.example.filetransport.conn.ConnManager;
import com.example.filetransport.sock.SocketManager;
import com.example.filetransport.transport.FileTransport;
import com.example.filetransport.utils.ConstantValues;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by yaojian on 2017/10/23.
 */

public class GetActivity extends AppCompatActivity {

    private TextView tv_show;

    private Thread connSocketThread;
    private boolean exit = false;
    private WifiManager wifiManager;
    private Socket socket;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstantValues.GET_CHANGE_DESC:
                    tv_show.setText((String)msg.obj);
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_receive);
        initView();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connSocketThread = new Thread(new ConnSocketThread());
        connSocketThread.start();
    }

    private void initView() {
        tv_show = (TextView) findViewById(R.id.tv_show);
    }

    private class ConnSocketThread implements Runnable {

        @Override
        public void run() {
            //開啓並鏈接wifi
            while (WifiManager.WIFI_STATE_ENABLED!=wifiManager.getWifiState()&&!exit){
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i("test","wifiState:"+wifiManager.getWifiState());
            ConnManager.sendHandler("wifi已經打開，開始鏈接",mHandler,
                    ConstantValues.GET_CHANGE_DESC);
            while (!connSocket()&&!exit){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ConnManager.sendHandler("鏈接失敗，正在重新連接",mHandler,
                        ConstantValues.GET_CHANGE_DESC);
            }
        }
    }

    private boolean connSocket() {
        try{
            socket = SocketManager.getInstance().connSocket();
            if (socket==null)
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        ConnManager.sendHandler("鏈接成功，可以發送文件了",mHandler,ConstantValues.GET_CHANGE_DESC);
        //開始接受文件
        while (FileTransport.getFile(socket,mHandler)&&!exit){
            ConnManager.sendHandler("文件傳輸完成",mHandler,ConstantValues.MAKE_TOAST);
            //等待再次傳輸
            ConnManager.sendHandler("等待發送端發送文件",mHandler,ConstantValues.GET_CHANGE_DESC);
        }
        if (!exit){
            //發送端關閉了鏈接
            ConnManager.sendHandler("發送端關閉了鏈接",mHandler,ConstantValues.MAKE_TOAST);
            onFinish();
        }
        return true;
    }

    private void onFinish() {
        exit = true;
        if (socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        onFinish();
    }
}
