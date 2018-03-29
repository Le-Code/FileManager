package com.example.shang.filemanager.conn;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import java.lang.reflect.InvocationTargetException;

import com.example.shang.filemanager.utils.ConstantValue;

import java.lang.reflect.Method;

/**
 * Created by yaojian on 2017/10/22.
 */

public class WifiApControl {

    public enum WIFI_AP_STATE{
        WIFI_AP_STATE_DISABLING,
        WIFI_AP_STATE_DISABLED, //不可用
        WIFI_AP_STATE_ENABLING, //正在用
        WIFI_AP_STATE_ENABLED,  //可用
        WIFI_AP_STATE_FAILED    //失败
    }

    /**
     * 打开手机热点
     * @param wifiManager
     * @return
     */
    public static boolean openWifiAp(WifiManager wifiManager){
        //关闭手机wifi
        WifiControl.closeWifi(wifiManager);
        if (getWifiApState(wifiManager)== WIFI_AP_STATE.WIFI_AP_STATE_DISABLED){
            return setWifiApEnabled(wifiManager,true);
        }else{
            //关闭热点
            closeWifiAp(wifiManager);
            return setWifiApEnabled(wifiManager,true);
        }
    }

    public static boolean closeWifiAp(WifiManager wifiManager) {
        if (getWifiApState(wifiManager)== WIFI_AP_STATE.WIFI_AP_STATE_ENABLED||
                getWifiApState(wifiManager)== WIFI_AP_STATE.WIFI_AP_STATE_ENABLING){
            //关闭wifi
            return setWifiApEnabled(wifiManager,false);
        }
        return true;
    }

    /**
     * 设置热点的状态
     * @param wifiManager
     * @param b 想要的状态
     * @return
     */
    public static boolean setWifiApEnabled(WifiManager wifiManager, boolean b) {
        //热点的配置类
        WifiConfiguration config = new WifiConfiguration();
        //设置热点的名称
        config.SSID = ConstantValue.WIFI_AP_NAME;
        //设置热点的密码
        config.preSharedKey = ConstantValue.WIFI_AP_PWD;
        //其余的相关配置
        config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.allowedKeyManagement
                .set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers
                .set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedGroupCiphers
                .set(WifiConfiguration.GroupCipher.TKIP);
        //通过反射调用函数开启热点
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled",
                WifiConfiguration.class, boolean.class);
            method.setAccessible(true);
            method.invoke(wifiManager, config, b);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获得热点的状态
     * @param wifiManager
     * @return
     */
    public static WIFI_AP_STATE getWifiApState(WifiManager wifiManager) {
        int tmp;
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            tmp = (int) method.invoke(wifiManager);
            if (tmp>10){
                tmp-=10;
            }
            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            e.printStackTrace();
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }
}
