package com.stay4it.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Stay on 13/7/15.
 * Powered by www.stay4it.com
 */
public class RequestManager {
    private static RequestManager mInstance;
    private final ExecutorService mExecutors;
    private HashMap<String, ArrayList<Request>> mCachedRequest;

    public static RequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new RequestManager();
        }
        return mInstance;
    }

    private RequestManager() {

        mCachedRequest = new HashMap<String, ArrayList<Request>>();
        mExecutors = Executors.newFixedThreadPool(5);

    }

    public void performRequest(Request request) {
        request.execute(mExecutors);
        if (!mCachedRequest.containsKey(request.tag)){
            ArrayList<Request> requests = new ArrayList<Request>();
            mCachedRequest.put(request.tag, requests);
        }
        mCachedRequest.get(request.tag).add(request);
    }

    public void cancelRequest(String tag) {
        cancelRequest(tag,false);
    }

    public void cancelRequest(String tag,boolean force) {
        if (tag == null || "".equals(tag.trim())) {
            return;
        }
//        TODO find requests by tag, and cancel them
        if (mCachedRequest.containsKey(tag)){
            ArrayList<Request> requests = mCachedRequest.remove(tag);
            for (Request request : requests) {
                if (!request.isCancelled && tag.equals(request.tag)) {
                    request.cancel(force);

                }
            }
        }

    }

    public void cancelAll() {
        for (Map.Entry<String,ArrayList<Request>> entry : mCachedRequest.entrySet()){
            ArrayList<Request> requests =  entry.getValue();
            for (Request request : requests) {
                if (!request.isCancelled) {
                    request.cancel(true);
                }
            }
        }
    }


}
