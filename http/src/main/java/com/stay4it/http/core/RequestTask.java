package com.stay4it.http.core;

import android.os.AsyncTask;
import android.os.Build;

import com.stay4it.http.Request;
import com.stay4it.http.error.AppException;
import com.stay4it.http.itf.OnProgressUpdatedListener;

import org.apache.http.HttpResponse;

import java.net.HttpURLConnection;

/**
 * Created by Stay on 28/6/15.
 * Powered by www.stay4it.com
 */
public class RequestTask extends AsyncTask<Void, Integer, Object> {

    private Request request;

    public RequestTask(Request request) {
        this.request = request;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Void... params) {
        if (request.iCallback != null) {
            Object o = request.iCallback.preRequest();
            if (o != null) {
                return o;
            }
        }
        return request(0);
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public Object request(int retry) {
        try {
//          FIXME: if you read the source code of Volley, you will also find this config
//            FIXME: you can also define a param in @Request to config outside
            if(Build.VERSION.SDK_INT >= 9){
                HttpURLConnection connection = HttpUrlConnectionUtil.execute(request, !request.enableProgressUpdated ? null : new OnProgressUpdatedListener() {
                    @Override
                    public void onProgressUpdated(int curLen, int totalLen) {
                        publishProgress(Request.STATE_UPLOAD, curLen, totalLen);
                    }
                });
                if (request.enableProgressUpdated) {
                    return request.iCallback.parse(connection, new OnProgressUpdatedListener() {
                        @Override
                        public void onProgressUpdated(int curLen, int totalLen) {
                            publishProgress(Request.STATE_DOWNLOAD,curLen, totalLen);
                        }
                    });
                } else {
                    return request.iCallback.parse(connection);
                }
            }else {
//                FIXME : you need third part lib to support upload files by using HttpClient
//                FIXME : so i didn't give an implementation, but you can simply find it on Google
                HttpResponse response = HttpClientUtil.execute(request);
                if (request.enableProgressUpdated) {
                    return request.iCallback.parse(response, new OnProgressUpdatedListener() {
                        @Override
                        public void onProgressUpdated(int curLen, int totalLen) {
                            publishProgress(Request.STATE_DOWNLOAD,curLen, totalLen);
                        }
                    });
                } else {
                    return request.iCallback.parse(response);
                }
            }
        } catch (AppException e) {
            if (e.type == AppException.ErrorType.TIMEOUT) {
                if (retry < request.maxRetryCount) {
                    retry++;
                    return request(retry);
                }
            }
            return e;
        }
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (o instanceof AppException) {
            if (request.onGlobalExceptionListener != null) {
                if (!request.onGlobalExceptionListener.handleException((AppException) o)) {
                    request.iCallback.onFailure((AppException) o);
                }
            } else {
                request.iCallback.onFailure((AppException) o);
            }
        } else {
            request.iCallback.onSuccess(o);
        }


    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        request.iCallback.onProgressUpdated(values[0], values[1],values[2]);

    }
}
