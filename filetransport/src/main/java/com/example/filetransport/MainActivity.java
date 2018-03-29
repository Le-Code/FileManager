package com.example.filetransport;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.filetransport.activity.GetActivity;
import com.example.filetransport.activity.SendActivity;
import com.example.filetransport.conn.WifiApControl;
import com.example.filetransport.conn.WifiControl;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_send;
    private Button btn_receive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_receive = (Button) findViewById(R.id.btn_receive);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_receive.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_receive:
                get();
                break;
            case R.id.btn_send:
                send();
                break;
            default:break;
        }
    }

    private void get() {
        //開啓wifi
        if (WifiControl.connWifi((WifiManager) getSystemService(Context.WIFI_SERVICE))){
            startActivity(new Intent(this, GetActivity.class));
        }
    }

    private void send() {
        //開啓熱點
        if (WifiApControl.openWifiAp((WifiManager) this.getSystemService(Context.WIFI_SERVICE))){
            startActivity(new Intent(this, SendActivity.class));
        }

    }
}
