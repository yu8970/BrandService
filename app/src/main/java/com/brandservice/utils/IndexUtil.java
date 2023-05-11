package com.brandservice.utils;

import com.brandservice.TaskResult;
import com.brandservice.TaskTarget;

import java.util.List;

public class IndexUtil {
    public static native List<TaskTarget> indexs_search(float[] query_vector, int k);

}
