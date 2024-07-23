package com.ancda.rtsppusher.common;

import com.qiniu.android.http.ResponseInfo;
import org.json.JSONObject;

/**
 * 作者： huangbiao
 * 时间： 2017-03-03
 */
public interface UploadCallback {
    public interface ResultCallback extends UploadCallback {
        void result(String key, ResponseInfo info, JSONObject res);
    }
    public interface ProgressCallback extends UploadCallback {
        void progress(String key, double percent);
    }
}
