package com.example.shang.filemanager.entity;

import java.io.File;

/**
 * Created by Shang on 2017/7/26.
 */
public class FileInformation {

    private String name;

    private File path;

    private boolean isDir;

    private int iconId;

    private String size;


    public FileInformation(String name, File path, boolean isDir, int iconId, String size) {
        this.name = name;
        this.path = path;
        this.isDir = isDir;
        this.iconId = iconId;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FileInformation{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", isDir=" + isDir +
                ", iconId=" + iconId +
                ", size='" + size + '\'' +
                '}';
    }
}
