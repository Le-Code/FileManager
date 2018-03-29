package com.example.shang.filemanager.Fragment.sendPager;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shang.filemanager.Fragment.FiveFragment;
import com.example.shang.filemanager.MainActivity;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.conn.ConnManager;
import com.example.shang.filemanager.conn.WifiApControl;
import com.example.shang.filemanager.dialog.ShowSendListDialog;
import com.example.shang.filemanager.entity.FileBeanSimple;
import com.example.shang.filemanager.sock.SockManager;
import com.example.shang.filemanager.transport.FileTransport;
import com.example.shang.filemanager.utils.ConstantValue;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yaojian on 2017/10/24.
 */

public class SendPager extends BaseSendPager implements View.OnClickListener {

    private Socket socket = null;
    private Thread apOpenThread;
    private WifiManager wifiManager;
    private boolean exit = false;

    private TextView tv_showState;
    private Button btn_sendFile;
    private Button btn_show_list;
    private TextView tv_showProgress;
    private ImageButton ibt_back_send;
    private Set<String>mSendFileList = new HashSet<>();

    private boolean isRunning = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstantValue.SEND_CHANGE_DESC:
                    tv_showState.setText((String)msg.obj);
                    break;
                case ConstantValue.PROCESS_CHANGED:
                    tv_showProgress.setText((String)msg.obj);
                    break;
                case ConstantValue.SEND_FINISHED:
                    Toast.makeText(mContext,"传输完成",Toast.LENGTH_SHORT).show();
                    ((MainActivity)fragment.getActivity()).removeSendFilePathListItem(
                            (String) msg.obj);
                    isRunning = false;
                default:break;
            }
        }
    };

    public SendPager(Fragment fragment) {
        super(fragment);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_send,null);
        tv_showState = (TextView) view.findViewById(R.id.tv_showState);
        tv_showProgress = (TextView) view.findViewById(R.id.tv_showProgress);
        btn_sendFile = (Button) view.findViewById(R.id.btn_sendFile);
        ibt_back_send = (ImageButton) view.findViewById(R.id.ibt_back_send);
        btn_show_list = (Button) view.findViewById(R.id.btn_show_list);
        wifiManager = (WifiManager) fragment.getActivity().getSystemService(Context.WIFI_SERVICE);
        initEvent();
        return view;
    }

    private void initEvent() {
        btn_sendFile.setOnClickListener(this);
        ibt_back_send.setOnClickListener(this);
        btn_show_list.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sendFile:
                if (isRunning){//正在发送
                    Toast.makeText(mContext,"当前正在发送，请稍后再试",Toast.LENGTH_SHORT).show();
                }else{
                    mSendFileList.clear();
                    mSendFileList.addAll(((MainActivity)fragment.getActivity()).getSendFilePathList());
                    sendFile(mSendFileList);
                }
                break;
            case R.id.ibt_back_send:
                onFinish();
                ((FiveFragment)fragment).resume();
                break;
            case R.id.btn_show_list:
                showListDialog();
                break;
            default:break;
        }
    }

    private void showListDialog() {
        List<String>list = new ArrayList<>();
        list.addAll(((MainActivity)fragment.getActivity()).getSendFilePathList());
        ShowSendListDialog dialog = new ShowSendListDialog(list,mContext,fragment);
        dialog.showDialog();
    }

    private void sendFile(final Set<String> filePathList) {
        if(filePathList.isEmpty()){
            Toast.makeText(mContext,"发送列表为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (socket!=null){
            isRunning = true;
            new Thread(){
                @Override
                public void run() {
                    for (String path:filePathList){
                        FileBeanSimple file = new FileBeanSimple(new File(path));
                        FileTransport.sendFile(file,socket,mHandler);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }else{
            Toast.makeText(mContext,"接收端尚未链接,请等待接受端链接后再次发送",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void initDate() {
        apOpenThread = new Thread(new TestApOpenThread());
        apOpenThread.start();
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
                    ,mHandler,ConstantValue.SEND_CHANGE_DESC);
            while (!exit){
                //開啓socket監聽
                try {
                    socket = SockManager.getInstance().listenSocket();
                    ConnManager.sendHandler("成功與接受端連接，可以發送文件",mHandler,
                            ConstantValue.SEND_CHANGE_DESC);
                } catch (IOException e) {
                    e.printStackTrace();
                    ConnManager.sendHandler("鏈接失敗，等待重連",mHandler,
                            ConstantValue.SEND_CHANGE_DESC);
                }
            }
        }
    }

    public void onFinish(){
        exit = true;
        if (socket!=null)
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        WifiApControl.closeWifiAp(wifiManager);
    }
}
