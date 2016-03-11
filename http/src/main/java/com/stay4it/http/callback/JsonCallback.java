package com.stay4it.http.callback;

import com.google.gson.Gson;
import com.stay4it.http.core.AbstractCallback;
import com.stay4it.http.error.AppException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Stay on 4/7/15.
 * Powered by www.stay4it.com
 */
public abstract class JsonCallback<T> extends AbstractCallback<T> {
    @Override
    protected T bindData(String result) throws AppException {
        try {
//            JSONObject json = new JSONObject(result);
//            JSONObject data = json.optJSONObject("data");
//            Gson gson = new Gson();
//            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//            return gson.fromJson(data.toString(), type);
            JSONObject json = new JSONObject(result);
            Object data = json.opt("data");
            Gson gson = new Gson();
            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return gson.fromJson(data.toString(), type);
        } catch (JSONException e) {
            throw new AppException(AppException.ErrorType.JSON,e.getMessage());
        }
    }
}
