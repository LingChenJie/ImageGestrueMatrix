package com.stay4it.http.itf;

import com.stay4it.http.error.AppException;

import org.apache.http.HttpResponse;

import java.net.HttpURLConnection;

/**
 * Created by Stay on 28/6/15.
 * Powered by www.stay4it.com
 */
public interface ICallback<T> {

    void onSuccess(T result);

    void onFailure(AppException e);

    /**
     * invoked on sub thread
     * @param t serialized by SubCallbacks
     * @return final result by calling onSuccess(t)
     */
    T postRequest(T t);

    /**
     *
     * invoked on sub thread
     * @return if not null, will skip the http request, call {@link #onSuccess(Object)} directly
     */
    T preRequest();

    T parse(HttpURLConnection connection, OnProgressUpdatedListener listener) throws AppException;

    T parse(HttpURLConnection connection) throws AppException;

    T parse(HttpResponse response, OnProgressUpdatedListener listener) throws AppException;

    T parse(HttpResponse response) throws AppException;

    void onProgressUpdated(int state,int curLen, int totalLen);

    void cancel();
}
