package com.example.filetransport.conn;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.example.filetransport.utils.ConstantValues;

import java.util.List;

/**
 * Created by yaojian on 2017/10/22.
 */

public class WifiControl {

    public enum WIFI_STATE{
        WIFI_STATE_ENABLED,
        WIFI_STATE_ENABLING,
        WIFI_STATE_DISABLING,
        WIFI_STATE_DISABLED,
        WIFI_STATE_UNKNOWN
    }

    public static boolean connWifi(WifiManager wifiManager){
        boolean b = openWifi(wifiManager);
        Thread thread = new Thread(new ConnectRunnable(wifiManager));
        thread.start();
        return b;
    }

    /**
     * 打开wifi
     * @param wifiManager
     * @return
     */
    public static boolean openWifi(WifiManager wifiManager){
        //关闭手机热点
        WifiApControl.closeWifiAp(wifiManager);
        if (!wifiManager.isWifiEnabled()){
            return wifiManager.setWifiEnabled(true);
        }
        return true;
    }

    /**
     * 关闭wifi
     * @param wifiManager
     * @return
     */
    public static boolean closeWifi(WifiManager wifiManager){
        if (wifiManager.isWifiEnabled()){
            return wifiManager.setWifiEnabled(false);
        }
        return true;
    }

    public static WIFI_STATE getWifiState(WifiManager wifiManager){
        return WIFI_STATE.class.getEnumConstants()[wifiManager.getWifiState()];
    }

    /**
     * 创建wifi配置
     * @param SSID
     * @param Password
     * @return
     */
    private static WifiConfiguration createWifiInfo(String SSID, String Password) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        //全部采用WPA加密
        config.preSharedKey = "\"" + Password + "\"";
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        // 此处需要修改否则不能自动重联
        // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }

    /**
     * 查看wifi是否配置过
     *
     * @param SSID
     * @param wifiManager
     * @return
     */
    public static WifiConfiguration isExsits(String SSID, WifiManager wifiManager) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if(existingConfigs!=null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }


    /**
     * 异步链接wifi
     */
    static class ConnectRunnable implements Runnable {
        private String ssid = ConstantValues.WIFI_AP_NAME;

        private String password =ConstantValues.WIFI_AP_PWD;
        private WifiManager wifiManager;

        public ConnectRunnable(WifiManager wifiManager) {
            this.wifiManager = wifiManager;
        }


        @Override
        public void run() {
            // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
            // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
            while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                try {
                    // 为了避免程序一直while循环，让它睡个100毫秒检测……
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
            WifiConfiguration wifiConfig = createWifiInfo(ssid, password);
            //
            if (wifiConfig == null) {
                return;
            }

            /**
             * 刷新wifi配置记录
             */
            WifiConfiguration tempConfig = isExsits(ssid, wifiManager);

            if (tempConfig != null) {
                wifiManager.removeNetwork(tempConfig.networkId);
            }
            int netID = wifiManager.addNetwork(wifiConfig);
            wifiManager.enableNetwork(netID, true);
        }
    }
}
