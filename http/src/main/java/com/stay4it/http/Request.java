package com.stay4it.http;

import android.os.Build;

import com.stay4it.http.core.RequestTask;
import com.stay4it.http.entities.FileEntity;
import com.stay4it.http.error.AppException;
import com.stay4it.http.itf.ICallback;
import com.stay4it.http.itf.OnGlobalExceptionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Stay on 24/6/15.
 * Powered by www.stay4it.com
 */
public class Request {
    public ICallback iCallback;
    public boolean enableProgressUpdated = false;
    public OnGlobalExceptionListener onGlobalExceptionListener;
    public String tag;
    private RequestTask task;

    public static final int STATE_UPLOAD = 1;
    public static final int STATE_DOWNLOAD = 2;


    public String filePath;
    public ArrayList<FileEntity> fileEntities;

    public void setCallback(ICallback iCallback) {
        this.iCallback = iCallback;
    }

    public void enableProgressUpdated(boolean enable) {
        this.enableProgressUpdated = enable;
    }

    public void setGlobalExceptionListener(OnGlobalExceptionListener onGlobalExceptionListener) {
        this.onGlobalExceptionListener = onGlobalExceptionListener;
    }

    public void checkIfCancelled() throws AppException {
        if (isCancelled){
            throw new AppException(AppException.ErrorType.CANCEL,"the request has been cancelled");
        }
    }

    public void cancel(boolean force) {
        isCancelled = true;
        iCallback.cancel();
        if (force && task != null){
            task.cancel(force);
        }
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void execute(Executor mExecutors) {
        task = new RequestTask(this);
        if (Build.VERSION.SDK_INT > 11){
            task.executeOnExecutor(mExecutors);
        }else {
            task.execute();
        }
    }




    public enum RequestMethod {GET, POST, PUT, DELETE}

    public int maxRetryCount = 3;
    public String url;
    public String content;
    public Map<String, String> headers;

    public volatile boolean isCancelled;

    public RequestMethod method;

    public Request(String url, RequestMethod method) {
        this.url = url;
        this.method = method;
    }

    public Request(String url) {
        this.url = url;
        this.method = RequestMethod.GET;
    }

    public void addHeader(String key, String value) {
        if (headers == null){
            headers = new HashMap<String,String>();
        }
        headers.put(key,value);
    }
}
