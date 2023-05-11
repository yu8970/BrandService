package com.brandservice.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.brandservice.IBrandServiceAIDL;
import com.brandservice.BrandResult;
import com.brandservice.TaskResult;
import com.brandservice.utils.DetectorUtil;
import com.brandservice.utils.SnowFlakeUtil;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import kotlin.io.ByteStreamsKt;

public class BrandService extends Service {
    private static final String TAG = "BrandService";

    private static final SnowFlakeUtil worker = new SnowFlakeUtil(1,1,1);

    private DetectorUtil detectorUtil;

    private IBinder iBinder = new IBrandServiceAIDL.Stub() {

        @Override
        public BrandResult getBrandResult(ParcelFileDescriptor pfd) throws RemoteException {
            Log.d(TAG, "[sendImageAndGetResult]");
            Long start = System.currentTimeMillis();

            FileDescriptor fileDescriptor = pfd.getFileDescriptor();
            FileInputStream fis = new FileInputStream(fileDescriptor);
            byte[] data = ByteStreamsKt.readBytes((InputStream)fis);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            BrandResult brandResult = new BrandResult();

            brandResult.setTraceId(worker.nextId());
            Date date = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
            brandResult.setCreateTime(dateFormat.format(date));

            /** detection **/
            TaskResult detectorResult = detectorUtil.startDetection(bitmap);
            brandResult.setDetectResult(detectorResult);
            /** detection **/

            /** search **/
            TaskResult searchResult = new TaskResult();
            // TODO
            brandResult.setSearchResult(searchResult);
            /** search **/

            try{
                fis.close();
            }catch (IOException e){

            }


            Long end = System.currentTimeMillis();
            brandResult.setTotalCostTime((int) (end-start));
            Log.d(TAG, "[sendImageAndGetResult]: "+brandResult.toString());
            return brandResult;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        detectorUtil = new DetectorUtil(getResources().getAssets(), BrandService.this);
        Log.e(TAG, "[onCreate]");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }


}
