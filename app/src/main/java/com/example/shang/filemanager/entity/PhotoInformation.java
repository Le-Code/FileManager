package com.example.shang.filemanager.entity;

/**
 * Created by yaojian on 2017/10/12.
 */
public class PhotoInformation {
    private String name;
    private String path;
    private String date;
    private String size;
    private String type;
    private String location;

    public PhotoInformation(String name, String path, String date, String size,
                            String type, String location) {
        this.name = name;
        this.path = path;
        this.date = date;
        this.size = size;
        this.type = type;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
