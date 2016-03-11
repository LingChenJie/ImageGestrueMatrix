package com.stay4it.http;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.stay4it.http.callback.JsonCallback;
import com.stay4it.http.core.RequestTask;

/**
 * Created by Stay on 24/6/15.
 * Powered by www.stay4it.com
 */
public class TestHttp extends InstrumentationTestCase {

    public void testHttpGet() throws Throwable {
        String url = "http://api.stay4it.com";
        Request request = new Request(url);
//        String result = HttpUrlConnectionUtil.execute(request);
//        Log.e("stay", "testHttpGet return:" + result);
    }

    public void testHttpPost() throws Throwable {
        String url = "http://api.stay4it.com/v1/public/core/?service=user.login";
        String content = "account=stay4it&password=123456";
        Request request = new Request(url, Request.RequestMethod.POST);
        request.content = content;
//        String result = HttpUrlConnectionUtil.execute(request);
//        Log.e("stay", "testHttpGet return:" + result);
    }

    public void testHttpPostOnSubThread() throws Throwable {

        String url = "http://api.stay4it.com/v1/public/core/?service=user.login";
        String content = "account=stay4it&password=123456";
        Request request = new Request(url, Request.RequestMethod.POST);
        request.setCallback(new JsonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e("stay", "testHttpGet return:" + result);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
        request.content = content;
        RequestTask task = new RequestTask(request);
        task.execute();
    }
}
