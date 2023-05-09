// IBrandServiceAIDL.aidl
package com.brandservice;
// Declare any non-default types here with import statements
import com.brandservice.BrandResult;
import com.brandservice.DetectorResult;
import com.brandservice.SearchResult;
import com.brandservice.DetectionTarget;
interface IBrandServiceAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    int sendImageAndGetResult(in ParcelFileDescriptor pfd);

    BrandResult getBrandResult(in ParcelFileDescriptor pfd);

}