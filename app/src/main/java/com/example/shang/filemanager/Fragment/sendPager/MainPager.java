package com.example.shang.filemanager.Fragment.sendPager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import com.example.shang.filemanager.Fragment.FiveFragment;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.conn.WifiApControl;
import com.example.shang.filemanager.conn.WifiControl;

/**
 * Created by yaojian on 2017/10/24.
 */

public class MainPager extends BaseSendPager implements View.OnClickListener {

    private LinearLayout ll_send;
    private LinearLayout ll_get;

    public MainPager(Fragment fragment) {
        super(fragment);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_send_main,null);
        ll_get = (LinearLayout) view.findViewById(R.id.ll_get);
        ll_send = (LinearLayout) view.findViewById(R.id.ll_send);
        initEvent();
        return view;
    }

    private void initEvent() {
        ll_get.setOnClickListener(this);
        ll_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_send:
                send();
                break;
            case R.id.ll_get:
                get();
                break;
            default:break;
        }
    }

    private void get() {
        if (WifiControl.connWifi(
                (WifiManager) fragment.getActivity().getSystemService(Context.WIFI_SERVICE))){
            GetPager getPager = new GetPager(fragment);
            getPager.initDate();
            ((FiveFragment)fragment).onChangeView(getPager.rootView);
        }
    }

    private void send() {
        if (WifiApControl.openWifiAp(
                (WifiManager) fragment.getActivity().getSystemService(Context.WIFI_SERVICE))){
            SendPager sendPager = new SendPager(fragment);
            sendPager.initDate();
            ((FiveFragment)fragment).onChangeView(sendPager.rootView);
        }else{
            requestWriteSettings();
        }
    }
    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + fragment.getActivity().getPackageName()));
        fragment.getActivity().startActivity(intent);
    }
}
