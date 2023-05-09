package mmdeploy;

import java.util.Arrays;

public class InstanceMask {
    public int[] shape;
    public char[] data;


    public InstanceMask(int height, int width, char[] data) {
        shape = new int[]{height, width};
        this.data = data;
    }

    @Override
    public String toString() {
        return "InstanceMask{" +
                "shape=" + Arrays.toString(shape) +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
