package com.example.shang.filemanager.utils;

import android.os.Environment;
import android.service.quicksettings.Tile;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by yaojian on 2017/10/26.
 */

public class FileUtils2 {

    public static Map<String, TreeSet<File>> getFileSort() {
        long start = System.nanoTime();
        File file = new File(Environment.getExternalStorageDirectory().getPath());
        if (file.exists()) {
            File[] files = file.listFiles();
            LinkedList<File> fileList = new LinkedList<>();
            for (File f : files) {
                fileList.add(f);
            }
            File tmpFile;
            Map<String, TreeSet<File>> mMap = new HashMap<>();
            TreeSet<File> musicSet = new TreeSet<>();
            TreeSet<File> imgSet = new TreeSet<>();
            TreeSet<File> filmSet = new TreeSet<>();
            while (!fileList.isEmpty()) {
                tmpFile = fileList.removeFirst();
                if (tmpFile.isDirectory()) {
                    files = tmpFile.listFiles();
                    for (File f : files) {
                        fileList.add(f);
                    }
                } else {
                    if (tmpFile.getName().matches(ConstantValue.IMAGE_MATCH)) {//图片
                        imgSet.add(tmpFile);
                    }
                    if (tmpFile.getName().matches(ConstantValue.MUSIC_MATCH)) {//音乐
                        musicSet.add(tmpFile);
                    }
                    if (tmpFile.getName().matches(ConstantValue.FILM_MATCH)) {
                        filmSet.add(tmpFile);
                    }
                }
            }
            mMap.put(ConstantValue.IMAGE, imgSet);
            mMap.put(ConstantValue.MUSIC, musicSet);
            mMap.put(ConstantValue.FILM, filmSet);
            long end = System.nanoTime();
            Log.d("test", "getFileSort: " + (end - start) / 1.0e9);
            return mMap;
        }
        return null;
    }

    public static Map<String, TreeSet<File>> getFileSortConcurrent(){
        long start = System.nanoTime();
        File rootFile = new File(Environment.getExternalStorageDirectory().getPath());
        File[] files = rootFile.listFiles();
        final List<Callable<Map<String,TreeSet<File>>>>partions = new ArrayList<>();
        for (final File file:files){
            partions.add(new Callable<Map<String, TreeSet<File>>>() {
                @Override
                public Map<String, TreeSet<File>> call() throws Exception {
                    return find(file);
                }
            });
        }
        Map<String, TreeSet<File>> mMap = new HashMap<>();
        mMap.put(ConstantValue.MUSIC,new TreeSet<File>());
        mMap.put(ConstantValue.IMAGE,new TreeSet<File>());
        mMap.put(ConstantValue.FILM,new TreeSet<File>());
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        double chunk = 0.8;
        int poolSize = (int) (numberOfCores/(1-chunk));
        ExecutorService executorPool = Executors.newFixedThreadPool(poolSize);
        try {
            List<Future<Map<String,TreeSet<File>>>>futures = executorPool.invokeAll(partions,1000, TimeUnit.SECONDS);
            for (final Future future:futures){
                Map<String,TreeSet<File>> tmpMap = (Map<String, TreeSet<File>>) future.get();
                if (tmpMap!=null){
                    mMap.get(ConstantValue.FILM).addAll(tmpMap.get(ConstantValue.FILM));
                    mMap.get(ConstantValue.MUSIC).addAll(tmpMap.get(ConstantValue.MUSIC));
                    mMap.get(ConstantValue.IMAGE).addAll(tmpMap.get(ConstantValue.IMAGE));
                }
            }
            long end = System.nanoTime();
            Log.d("testaa", "getFileSort: " + (end - start) / 1.0e9);
            return mMap;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String,TreeSet<File>> find(File rootFile){
        if (!rootFile.exists()){
            return null;
        }
        LinkedList<File> linkedList = new LinkedList<>();
        Map<String, TreeSet<File>> mMap = new HashMap<>();
        TreeSet<File> musicSet = new TreeSet<>();
        TreeSet<File> imgSet = new TreeSet<>();
        TreeSet<File> filmSet = new TreeSet<>();
        linkedList.add(rootFile);
        File tmpFile;
        File[] files;
        while (!linkedList.isEmpty()) {
            tmpFile = linkedList.removeFirst();
            if (tmpFile.isDirectory()) {
                files = tmpFile.listFiles();
                for (File f : files) {
                    linkedList.add(f);
                }
            } else {
                if (tmpFile.getName().matches(ConstantValue.IMAGE_MATCH)) {//图片
                    imgSet.add(tmpFile);
                }
                if (tmpFile.getName().matches(ConstantValue.MUSIC_MATCH)) {//音乐
                    musicSet.add(tmpFile);
                }
                if (tmpFile.getName().matches(ConstantValue.FILM_MATCH)) {
                    filmSet.add(tmpFile);
                }
            }
        }
        mMap.put(ConstantValue.IMAGE, imgSet);
        mMap.put(ConstantValue.MUSIC, musicSet);
        mMap.put(ConstantValue.FILM, filmSet);
        return mMap;
    }
}
