package com.brandservice.utils;

import android.graphics.Bitmap;

public class ModelUtil {
    //推理方法
    public static native float[] infer(Bitmap img);
}
