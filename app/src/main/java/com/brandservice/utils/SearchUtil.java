package com.brandservice.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.brandservice.TaskResult;
import com.brandservice.TaskTarget;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
        searchResult.setCostTime(100);
        List<TaskTarget> searchTargets = new ArrayList<>();
        for (int i = 0; i < 4;i++)
        {   TaskTarget st = new TaskTarget();
            st.setId(String.valueOf(i));
            st.setName(String.valueOf(i)+"Name");
            st.setScore(80);
            String img_path = "image/background.png";
            Bitmap backBitmap = null;
            try {
                InputStream is = am.open(img_path);
                backBitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                Log.e("bitmap", "getImageFromAssetsFile: IOException!");
            }
            st.setImage( bitmap);
            searchTargets.add(st);
        }
        searchResult.setTargets(searchTargets);


        return searchResult;
     }


}
