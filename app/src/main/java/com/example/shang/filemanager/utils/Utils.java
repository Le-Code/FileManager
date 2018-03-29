package com.example.shang.filemanager.utils;

import com.example.shang.filemanager.R;
import com.example.shang.filemanager.entity.FileInformation;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Shang on 2017/7/24.
 */
public class Utils {

    private List<FileInformation> aList = new LinkedList<FileInformation>();

    public List<FileInformation> getFileList(String file){
        List<Map<String, Object>> list = FileUtils.getInstance().getSonNode(file);//FileUtils.getInstance().getBasePath()
        if (list != null) {
            Collections.sort(list, FileUtils.getInstance().defaultOrder());
            aList.clear();
            for (Map<String, Object> map : list) {
                String fileType = (String) map.get(FileUtils.FILE_INFO_TYPE);
                String name = (String) map.get(FileUtils.FILE_INFO_NAME);
                File path = (File) map.get(FileUtils.FILE_INFO_PATH);
                boolean isDir;
                int iconId;
                String size;
                if (map.get(FileUtils.FILE_INFO_ISFOLDER).equals(true)) {
                    isDir = true;
                    iconId = R.drawable.dir;
                    size = map.get(FileUtils.FILE_INFO_NUM_SONDIRS) + "个文件夹和" +
                            map.get(FileUtils.FILE_INFO_NUM_SONFILES) + "个文件";
                } else {
                    isDir = false;
                    iconId = R.drawable.txt;
//                    if (fileType.equals("txt") || fileType.equals("text")) {
//                        iconId = R.drawable.txt;
//                    } else {
//                        iconId = R.drawable.dir;
//                    }
                    size = FileUtils.getInstance().getFileSize(map.get(FileUtils.FILE_INFO_PATH).toString());
                }
                FileInformation information = new FileInformation(name, path, isDir, iconId, size);
                System.out.println("==" + information);
                aList.add(information);
            }
        } else {
            aList.clear();
        }
        return aList;
    }
}
