package com.example.filetransport.sock;

import com.example.filetransport.utils.ConstantValues;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by yaojian on 2017/10/23.
 */

public class SocketManager {
    private ServerSocket serverSocket;
    private Socket socket;

    private SocketManager() throws IOException {
        serverSocket = new ServerSocket(ConstantValues.AP_PORT);
    }

    private static SocketManager instance;

    public static SocketManager getInstance(){
        if(instance==null)
            try {
                instance = new SocketManager();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return instance;
    }

    /**
     * 根據ip鏈接socket
     */
    public Socket connSocket() throws IOException {
        //創建一個新的socket實例來接連
        if (socket!=null)
            socket.close();
        socket = new Socket();
        SocketAddress remoteAddress = new InetSocketAddress(
                ConstantValues.AP_IP,ConstantValues.AP_PORT);
        socket.connect(remoteAddress,3000);
        return socket;
    }

    /**
     * 持續監聽socket
     * @return socket
     */
    public Socket listenSocket() throws IOException {
        socket = serverSocket.accept();
        return socket;
    }

    /**
     * 關閉socket
     */
    public boolean closeSocket(){
        if (socket!=null)
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        return true;
    }

    /**
     * 關閉serverSocket
     * @return 成功返回true,失敗返回false
     */
    public boolean closeServerSocket(){
        if (serverSocket!=null)
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        return true;
    }
}
