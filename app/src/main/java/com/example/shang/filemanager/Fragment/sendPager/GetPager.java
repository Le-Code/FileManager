package com.example.shang.filemanager.Fragment.sendPager;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shang.filemanager.Fragment.FiveFragment;
import com.example.shang.filemanager.MainActivity;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.conn.ConnManager;
import com.example.shang.filemanager.conn.WifiControl;
import com.example.shang.filemanager.sock.SockManager;
import com.example.shang.filemanager.transport.FileTransport;
import com.example.shang.filemanager.utils.ConstantValue;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by yaojian on 2017/10/24.
 */

public class GetPager extends BaseSendPager implements View.OnClickListener {

    private WifiManager wifiManager;
    private Socket socket;
    private Thread connSocketThread;
    private boolean exit = false;

    private TextView tv_show;
    private ImageButton ibt_back_get;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstantValue.GET_CHANGE_DESC:
                    tv_show.setText((String)msg.obj);
                    break;
                case ConstantValue.MAKE_TOAST:
                    Toast.makeText(mContext,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }
        }
    };

    public GetPager(Fragment fragment) {
        super(fragment);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_get,null);
        tv_show = (TextView) view.findViewById(R.id.tv_show);
        ibt_back_get = (ImageButton) view.findViewById(R.id.ibt_back_get);
        wifiManager = (WifiManager) fragment.getActivity().getSystemService(Context.WIFI_SERVICE);
        initEvent();
        return view;
    }

    private void initEvent() {
        ibt_back_get.setOnClickListener(this);
    }

    @Override
    public void initDate() {
        connSocketThread = new Thread(new ConnSocketThread());
        connSocketThread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibt_back_get:
                onFinish();
                ((FiveFragment)fragment).resume();
        }
    }

    class ConnSocketThread implements Runnable{
        @Override
        public void run() {
            while (wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //wifi已经打开
            ConnManager.sendHandler("wifi已经打开，正在连接",mHandler, ConstantValue.GET_CHANGE_DESC);
            while (!connSocket()&&!exit){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ConnManager.sendHandler("鏈接失敗，正在重新連接",mHandler,
                        ConstantValue.GET_CHANGE_DESC);
            }

        }
    }

    private boolean connSocket() {
        try{
            socket = SockManager.getInstance().connSocket();
            if (socket==null)
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        ConnManager.sendHandler("连接成功，可以发送文件了",mHandler,ConstantValue.GET_CHANGE_DESC);
        //开始接受文件
        while (FileTransport.getFile(socket,mHandler)&&!exit){
            //文件传输完成
            ConnManager.sendHandler("文件传输完成",mHandler,ConstantValue.GET_CHANGE_DESC);
            //等待下次传输
            ConnManager.sendHandler("等待发送端发送文件",mHandler,ConstantValue.GET_CHANGE_DESC);
        }
        if (!exit){
            //發送端關閉了鏈接
            ConnManager.sendHandler("發送端關閉了鏈接",mHandler,ConstantValue.MAKE_TOAST);
            onFinish();
        }
        return true;
    }

    private void onFinish() {
        exit = true;
        if (socket!=null)
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        WifiControl.closeWifi(wifiManager);
    }
}
