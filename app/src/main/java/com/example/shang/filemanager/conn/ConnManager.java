package com.example.shang.filemanager.conn;

import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

/**
 * Created by yaojian on 2017/10/22.
 */

public class ConnManager {
    private WifiManager wifiManager;
    private ConnManager(WifiManager wifiManager){
        this.wifiManager = wifiManager;
    }

    private static ConnManager instance;

    public static ConnManager getInstance(WifiManager wifiManager){
        if (instance==null)
            instance = new ConnManager(wifiManager);
        return instance;
    }

    public static void sendHandler(Object obj, Handler handler,int what){
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }
}
