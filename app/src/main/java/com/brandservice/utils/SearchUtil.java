package com.brandservice.utils;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.brandservice.TaskResult;
import com.brandservice.TaskTarget;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchUtil {
    static TaskResult searchResult = new TaskResult();
    public static AssetManager am;

    public static TaskResult search(Bitmap bitmap,int k){
         //先将图片进行推理
//        long start = System.currentTimeMillis();
//        float[] queryVec = ModelUtil.infer(bitmap);
//        List<SearchTarget> searchTargets =IndexUtil.indexs_search(queryVec,k);
//        long end = System.currentTimeMillis();
//        searchResult.setCost_time((int) (end-start));
//        searchResult.setSearchTargets(searchTargets);
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        searchResult.setCreateTime(dateFormat.format(date));
        searchResult.setCostTime(100);
        List<TaskTarget> searchTargets = new ArrayList<>();
        for (int i = 0; i < 4;i++)
        {   TaskTarget st = new TaskTarget();
            st.setId(String.valueOf(i));
            st.setName("demo"+i);
            st.setScore(0.80f);
            st.setImage( bitmap);
            searchTargets.add(st);
        }
        searchResult.setTargets(searchTargets);
        searchResult.setHasResult(!searchResult.getTargets().isEmpty());

        return searchResult;
     }


}
