package mmdeploy;

import android.util.Log;

public class Detector {

    private static final String TAG = "Detector";

    static {
        // libmmdeploy_java.so
        Long start = System.currentTimeMillis();
        System.loadLibrary("mmdeploy_java");
        Long end = System.currentTimeMillis();
        Log.e(TAG, "[loadLibrary] loadLibrary cost time:"+(end-start)+"ms");
    }

    public static class Result {
        private final int label_id;
        private final float score;
        private final Rect bbox;
        private final InstanceMask mask;
        public Result(int label_id, float score, Rect bbox, InstanceMask mask) {
            this.label_id = label_id;
            this.score = score;
            this.bbox = bbox;
            this.mask = mask;
        }

        public int getLabel_id() {
            return label_id;
        }

        public float getScore() {
            return score;
        }

        public Rect getBbox() {
            return bbox;
        }

        public InstanceMask getMask() {
            return mask;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "label_id=" + label_id +
                    ", score=" + score +
                    ", bbox=" + bbox +
                    ", mask=" + mask +
                    '}';
        }
    }


    private final long handle;

    public Detector(String modelPath, String deviceName, int deviceId) {
        handle = create(modelPath, deviceName, deviceId);
    }

    public Result[][] apply(Mat[] images) {
        int[] counts = new int[images.length];
        Result[] results = apply(handle, images, counts);

        Result[][] rets = new Result[images.length][];
        int offset = 0;
        for (int i = 0; i < images.length; ++i) {
            Result[] row = new Result[counts[i]];
            if (counts[i] >= 0) {
                System.arraycopy(results, offset, row, 0, counts[i]);
            }
            offset += counts[i];
            rets[i] = row;
        }
        return rets;
    }

    public Result[] apply(Mat image) {
        int[] counts = new int[1];
        Mat[] images = new Mat[]{image};
        Long start = System.currentTimeMillis();
        Result[] results = apply(handle, images, counts);
        Long end = System.currentTimeMillis();
        Log.e(TAG, "[apply] cost time:"+(end-start)+"ms");
        return results;
    }

    public void release() {
        destroy(handle);
    }

    private native long create(String modelPath, String deviceName, int deviceId);

    private native void destroy(long handle);

    private native Result[] apply(long handle, Mat[] images, int[] count);
}
