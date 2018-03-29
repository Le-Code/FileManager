package com.example.shang.filemanager.transport;

import android.os.Handler;

import com.example.shang.filemanager.conn.ConnManager;
import com.example.shang.filemanager.entity.FileBeanSimple;
import com.example.shang.filemanager.utils.ConstantValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;

/**
 * Created by yaojian on 2017/10/23.
 */

public class FileTransport {

    /**
     * 發送文件到另一個socket
     * @param file 發送的文件
     * @param socket
     * @return
     */
    public static boolean sendFile(FileBeanSimple file, Socket socket, Handler handler){
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(file.getFile());
            os.write((file.getFileName()+ ConstantValue.SPLIT+String.valueOf(file.getRawSize())
                    +ConstantValue.SPLIT).getBytes());
            os.flush();
            byte [] bytes = new byte[ConstantValue.SEND_BUFFER_SIZE];
            int len,sendLen = 0;
            while ((len = fis.read(bytes))!=-1){
                os.write(bytes,0,len);
                sendLen+=len;
                //修改界面
                ConnManager.sendHandler(
                        new DecimalFormat("###0.00").format((sendLen / file.getRawSize()) * 100),
                        handler, ConstantValue.PROCESS_CHANGED);
                os.flush();
            }
            ConnManager.sendHandler(file.getPath(),handler,ConstantValue.SEND_FINISHED);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 接受傳輸斷發送來的對象
     * @param socket
     * @param handler
     * @return
     */
    public static boolean getFile(Socket socket, Handler handler){

        InputStream is = null;
        File file = null;
        OutputStream fos = null;
        try {
            is = socket.getInputStream();
            String name="";//文件名
            int count = 0;//分离次数
            int cacheLength = 0;//缓存大小
            int tempLength  = 0;//暂时存储空间
            byte[] temp = new byte[1024];//暂时存储空间
            double size = 0;//文件大小
            double getsize = 0;//已经得到的文件大小
            byte[] cache = new byte[1024];//缓存
            byte[] buffer = new byte[ConstantValue.GET_BUFFER_SIZE];
            int length = 0;

            cacheLength = is.read(cache);//读取数据进如缓存进行分析处理
            //分割处理
            String[] values = (new String(cache)).split(ConstantValue.SPLIT);
//            Loger.takeLog(cacheLength+"");
//            Loger.takeLog(new String(cache));
            //发送端没有关闭
            if(cacheLength!=-1) {
                //while循環用來獲取接收到的文件名
                while (values.length < 3) {
                    tempLength = is.read(temp);
                    for (int i = cacheLength; i < 1024; i++) {
                        cache[i] = temp[i];
                        cacheLength += tempLength;
                    }
                    values = (new String(cache)).split(ConstantValue.SPLIT);
//                    Loger.takeLog(cacheLength + "");
//                    Loger.takeLog(new String(cache));
                    if (values.length >= 3) {
                        break;
                    }
                    if (cacheLength >= 1000) {
//                        Loger.takeLog("不要传那么大的文件名,都超过1k字了");
                    }
                }
                name = values[0];
                size = new Double(values[1]);
                ConnManager.sendHandler(name+","+size,handler,ConstantValue.GET_START);
                ConnManager.sendHandler("正在接收文件", handler, ConstantValue.GET_CHANGE_DESC);
                ConnManager.sendHandler(name,handler,ConstantValue.FILE_CHANGED);
                //新建文件
//                Loger.takeLog("开始处理文件");
                file = creatFile(ConstantValue.STORE_PATH, name);
                fos = new FileOutputStream(file);
                //处理缓存中的正文数据
                int headLength = (name + ConstantValue.SPLIT + values[1]
                        + ConstantValue.SPLIT).getBytes().length;
                int remainLength = cacheLength - headLength;
                getsize = remainLength;
                fos.write(cache, headLength, (int) getsize);
                fos.flush();
//                Loger.takeLog("继续接受文件");
                //正文处理完毕，开始继续接受

//                Loger.takeLog(getsize + "/" + size);
                while (getsize < size) {
                    length = is.read(buffer);
                    getsize += length;
//                    Loger.takeLog(length + "");
                    if (length == -1)
                        break;
                    fos.write(buffer, 0, length);
                    //修改界面
                    ConnManager.sendHandler(
                            new DecimalFormat("###0.00").format((getsize / size) * 100),
                            handler, ConstantValue.PROCESS_CHANGED);
                    fos.flush();
                }
                fos.close();
//                Loger.takeLog("传输完了");
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
//        Loger.takeLog("客户端已经关闭链接了");
        return false;
    }

    /**
     * 創建一個文件
     * 如果重名則在當前文件名後面加上下劃綫
     * @param path 路徑
     * @param name 名稱
     * @return
     * @throws IOException
     */
    public static File creatFile(String path,String name) throws IOException {
        File file = new File(path);
        if (!file.exists())
            file.mkdir();
        file = new File(path,name);
        if(file.exists()){
            String[] names = name.split("\\.");
            if(names.length==2){
                name = names[0]+"_"+"."+names[1];
            }
            else
                name+="_";
            return creatFile(path,name);
        }else {
            file.createNewFile();
        }
        return file;
    }
}
