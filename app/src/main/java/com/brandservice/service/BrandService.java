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
import com.brandservice.utils.SearchUtil;
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
            FileDescriptor fileDescriptor = pfd.getFileDescriptor();
            FileInputStream fis = new FileInputStream(fileDescriptor);
            byte[] data = ByteStreamsKt.readBytes((InputStream)fis);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            BrandResult result = getResult(bitmap);
            try{
                fis.close();
            }catch (IOException e){

            }
            return result;
        }

        @Override
        public BrandResult getBrandResultWithBitmap(Bitmap bitmap) throws RemoteException {
            return getResult(bitmap);
        }

        private BrandResult getResult(Bitmap bitmap){
            Long start = System.currentTimeMillis();
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
            int k = 4;
            SearchUtil.am = getResources().getAssets();
            TaskResult searchResult = SearchUtil.search(bitmap,k);;
            // TODO
            brandResult.setSearchResult(searchResult);
            /** search **/


            Long end = System.currentTimeMillis();
            brandResult.setTotalCostTime((int) (end-start));
            Log.d("[BrandService]", brandResult.toString());
            return brandResult;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        detectorUtil = new DetectorUtil(getResources().getAssets(), BrandService.this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }


}
