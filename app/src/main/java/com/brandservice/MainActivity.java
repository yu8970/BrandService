package com.brandservice;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.brandservice.databinding.ActivityMainBinding;
import com.brandservice.utils.DetectorUtil;
import com.brandservice.utils.PermissionUtil;


public class MainActivity extends AppCompatActivity {
    private final Integer takePhoto = 1;
    private final Integer fromAlbum = 2;
    private ActivityMainBinding binding;
    private File outputImage;
    private Uri imageUri;
    private DetectorUtil detectorUtil;


    @Override
    protected void onStart() {
        super.onStart();
        PermissionUtil.checkPermission(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PermissionUtil.isPermissionGranted(this)) {
            Log.i("PERMISSION","请求权限成功");
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission","授权失败！");
                    // 授权失败，退出应用
                    this.finish();
                    return;
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // init detector
        detectorUtil = new DetectorUtil(getResources().getAssets(), MainActivity.this);
        //initCamera();
        binding.takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outputImage = new File(getExternalCacheDir(), "output_iamge.jpg");
                if(outputImage.exists()){
                    outputImage.delete();
                }
                try {
                    outputImage.createNewFile();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        imageUri = FileProvider.getUriForFile(MainActivity.this, "mmdeploy.fileprovider", outputImage);
                    }else{
                        imageUri = Uri.fromFile(outputImage);
                    }
                    // 启动相机
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, takePhoto);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        binding.fromAlbumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 打开文件选择器
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // 指定只显示图片
                intent.setType("image/*");
                startActivityForResult(intent, fromAlbum);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == takePhoto){
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                Bitmap rotateBitmap = rotateIfRequired(bitmap);
                /* 此处调用检测算法 */
                Bitmap result = detectorUtil.drawDetection(rotateBitmap);
                binding.imageView.setImageBitmap(result);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else if (requestCode == fromAlbum){
            if(resultCode == Activity.RESULT_OK && data != null){
                try {
                    Bitmap bitmap = getBitmapFromUri(data.getData());

                    /* 此处调用检测算法 */
                    Bitmap result = detectorUtil.drawDetection(bitmap);
                    binding.imageView.setImageBitmap(result);

                    //binding.brandImageView.setImageBitmap(demoBitmap);

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws FileNotFoundException {
        ParcelFileDescriptor descriptor = this.getContentResolver().openFileDescriptor(uri, "r");
        return BitmapFactory.decodeFileDescriptor(descriptor.getFileDescriptor());
    }
    private Bitmap rotateIfRequired(Bitmap bitmap) throws IOException {
        ExifInterface exif = new ExifInterface(outputImage.getPath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Bitmap resBitmap;
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                resBitmap = rotateBitmap(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                resBitmap = rotateBitmap(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                resBitmap = rotateBitmap(bitmap, 270);
                break;
            default:
                resBitmap = bitmap;
                break;
        }
        return resBitmap;
    }
    private Bitmap rotateBitmap(Bitmap bitmap, int degree){
        Matrix matrix = new Matrix();
        matrix.postRotate((float) degree);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return rotatedBitmap;
    }

}
