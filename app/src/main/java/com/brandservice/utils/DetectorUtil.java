package com.brandservice.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.brandservice.DetectionTarget;
import com.brandservice.DetectorResult;
import com.brandservice.service.BrandService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import mmdeploy.DataType;
import mmdeploy.Detector;
import mmdeploy.Mat;
import mmdeploy.MultiBoxTracker;
import mmdeploy.PixelFormat;
import mmdeploy.Rect;

public class DetectorUtil {
    private static final String TAG = "DetectorUtil";
    private static final String [] modelTypes = {"first", "second","third"};
    private String deviceName;

    private int currentModel;

    private MultiBoxTracker tracker;

    private Detector detector;

    private String workDir;

    private AssetManager am;


    public DetectorUtil( AssetManager assetManager, final Context context){
        this.deviceName = "cpu";
        this.currentModel = 0;
        this.workDir = getBasePath();
        this.am = assetManager;
        this.tracker = new MultiBoxTracker(context);
        if (ResourceUtils.copyFileFromAssets("dump_info", workDir)){
            String modelPath=workDir + "/" + modelTypes[currentModel];
            this.detector = new Detector(modelPath, deviceName, 0);
        }
    }


    public DetectorResult startDetection(Bitmap srcImg){
        // 封装结果
        DetectorResult detectorResult = new DetectorResult();

        // 请求时间
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        detectorResult.setCreateTime(dateFormat.format(date));

        // 检测耗时
        long start = System.currentTimeMillis();
        srcImg = scaleBitmap(srcImg);
        List<Detector.Result> res = getDetectionResult(srcImg);
        long end = System.currentTimeMillis();
        detectorResult.setCostTime((int) (end-start));

        // 绘制原图
        tracker.trackResults(res);
        Bitmap noCutBitmap = srcImg.copy(Bitmap.Config.ARGB_8888, true);
        tracker.draw(new Canvas(noCutBitmap));
        Bitmap cutBitmap = cutBitmap(noCutBitmap, srcImg.getWidth(), srcImg.getHeight());
        detectorResult.setDetectedImage(cutBitmap);
        // 裁剪目标
        detectorResult.setTargets(resToTarget(srcImg, res));
        // 判断是否检测到目标
        detectorResult.setHasResult(!res.isEmpty());

        return detectorResult;
    }

    public Bitmap drawDetection(Bitmap srcImg){
        srcImg = scaleBitmap(srcImg);
        List<Detector.Result> filteredResult = getDetectionResult(srcImg);
        tracker.trackResults(filteredResult);
        Bitmap tempBitmap = srcImg.copy(Bitmap.Config.ARGB_8888, true);
        tracker.draw(new Canvas(tempBitmap));
        return cutBitmap(tempBitmap, srcImg.getWidth(), srcImg.getHeight());
    }


    private List<DetectionTarget> resToTarget(Bitmap noCutBitmap, List<Detector.Result> results){
        List<DetectionTarget> targets = new ArrayList<>();
        for(Detector.Result result: results){
            DetectionTarget target = new DetectionTarget();
            // TODO 商标注册ID，暂时无数据，默认10001
            target.setBrandId("10001");
            target.setBrandName(MultiBoxTracker.CLASSNAME[result.getLabel_id()]);
            target.setScore(result.getScore());
            Rect box = result.getBbox();
            target.setImage(Bitmap.createBitmap(noCutBitmap, (int) box.left, (int) box.top, (int) (box.right-box.left), (int) (box.bottom-box.top), null, false));
            targets.add(target);
        }
        return targets;
    }

    public List<Detector.Result> getDetectionResult(Bitmap srcBitmap){
        Log.d(TAG, "[getDetectionResult]");
        long start = System.currentTimeMillis();
        // 最长边为640，否则报错，必须先缩放
        int tempEdge = Math.max(srcBitmap.getWidth(), srcBitmap.getHeight());
        if(tempEdge != 640) {
            throw new RuntimeException("width or height not 640!");
        }
        // 调整为正方形
        Bitmap bitmap = paddingBitmap(srcBitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixArr = new int[width*height];
        // bitmap to arr
        bitmap.getPixels(pixArr,0,width,0,0,width,height);
        byte [] bgra = pixArrToBgra(pixArr, width, height);
        byte [] data = BgraToBgr(bgra, width, height);
        Mat rgb  = new Mat(bitmap.getHeight(), bitmap.getWidth(), 3, PixelFormat.BGR, DataType.INT8, data);
        // 检测
        Detector.Result[] result = detector.apply(rgb);
        // 结果处理
        List<Detector.Result> filteredResult = filterResult(result, 0.4f, true);

        long end = System.currentTimeMillis();
        Log.d(TAG,"[detect cost time]: "+(end-start)+" ms");

        return filteredResult;
    }

    private Bitmap scaleBitmap(Bitmap srcBitmap){
        // 等比例缩放到640，否则分辨率过大，内存溢出
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();

        float scaleW1 = 640.0f / srcWidth;
        float scaleH1 = 640.0f / srcHeight;
        float mimScale = Math.min(scaleW1, scaleH1);
        Matrix matrix1 = new Matrix();
        matrix1.postScale(mimScale, mimScale);
        srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth, srcHeight, matrix1, true);
        return srcBitmap;
    }

    private Bitmap paddingBitmap(Bitmap srcBitmap) {
        // 先获取背景bitmap
        String img_path = "image/background.png";
        Bitmap backBitmap = null;
        try {
            InputStream is = am.open(img_path);
            backBitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            Log.e("bitmap", "getImageFromAssetsFile: IOException!");
        }
        int maxEdge = Math.max(srcBitmap.getWidth(), srcBitmap.getHeight());
        Matrix matrix  = new Matrix();
        float scaleW = ((float) maxEdge) / backBitmap.getWidth();
        float scaleH = ((float) maxEdge) / backBitmap.getHeight();
        //取宽高最大比例来缩放图片
        float max = Math.max(scaleW, scaleH);
        matrix.postScale(max, max);
        Bitmap newBackBitmap = Bitmap.createBitmap(backBitmap, 0, 0, backBitmap.getWidth(), backBitmap.getHeight(), matrix, true);
        Canvas canvas = new Canvas(newBackBitmap);
        canvas.drawBitmap(srcBitmap, 0, 0, null);
        return newBackBitmap;
    }

    private Bitmap cutBitmap(Bitmap bitmap, int width, int height){
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, null, false);
    }
    private byte[] pixArrToBgra(int [] pixArray, int w, int h) {
        byte[] bgra = new byte [w * h * 4];
        for (int i = 0; i < w * h; i++) {
            byte a = (byte)(pixArray[i] >> 24);
            byte r = (byte)((pixArray[i] >> 16) & 0x000000ff);
            byte g = (byte)((pixArray[i] >> 8) & 0x000000ff);
            byte b = (byte)(pixArray[i] & 0x000000ff);
            bgra[i * 4] = b;
            bgra[i * 4 + 1] = g;
            bgra[i * 4 + 2] = r;
            bgra[i * 4 + 3] = a;
        }
        return bgra;
    }

    private byte[] BgraToBgr(byte[] bgra, int w, int h) {
        byte[] pixels = new byte[(bgra.length / 4) * 3];
        int count = bgra.length / 4;

        for (int i = 0; i < count; i++) {
            pixels[i * 3] = bgra[i * 4];    //B
            pixels[i * 3 + 1] = bgra[i * 4 + 1];//G
            pixels[i * 3 + 2] = bgra[i * 4 + 2];    //R
        }
        return pixels;
    }

    private String getBasePath() {
        return PathUtils.getExternalAppFilesPath() + File.separator + "file";
    }
    private List<Detector.Result> filterResult(Detector.Result[] result, float threshold, boolean isUnique){
        List<Detector.Result> results = Arrays.asList(result);
        // 过滤低于阈值的
        List<Detector.Result> collect = results.parallelStream().filter(result1 -> result1.getScore() >= threshold).collect(Collectors.toList());
        // 结果去重，同样id只保留score最大的
        if(isUnique){
            Map<Integer, Detector.Result> map = new HashMap<>();
            for(Detector.Result res : collect){
                if(res.getBbox() == null) continue;
                int id = res.getLabel_id();
                float score = res.getScore();
                if(map.containsKey(id)){
                    if(Objects.requireNonNull(map.get(id)).getScore() < score) map.put(id, res);
                }else{
                    map.put(id, res);
                }
            }
            collect = new ArrayList<>(map.values());
        }

        return collect;
    }

}
