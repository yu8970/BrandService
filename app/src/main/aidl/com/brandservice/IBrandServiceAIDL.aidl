package com.brandservice;

import com.brandservice.BrandResult;
import com.brandservice.TaskResult;
import com.brandservice.TaskTarget;

interface IBrandServiceAIDL {

    BrandResult getBrandResult(in ParcelFileDescriptor pfd);

    BrandResult getBrandResultWithBitmap(in Bitmap bitmap);

}