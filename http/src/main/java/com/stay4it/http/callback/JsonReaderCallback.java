package com.stay4it.http.callback;

import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.stay4it.http.core.AbstractCallback;
import com.stay4it.http.error.AppException;
import com.stay4it.http.entities.IEntity;

import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Stay on 4/7/15.
 * Powered by www.stay4it.com
 */
public abstract class JsonReaderCallback<T extends IEntity> extends AbstractCallback<T> {
    @Override
    protected T bindData(String path) throws AppException {
        try {

            Log.e("stay", "JsonReaderCallback bindData:" + path);
//            JSONObject json = new JSONObject(result);
//            JSONObject data = json.optJSONObject("data");
//            Gson gson = new Gson();
//            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//            return gson.fromJson(data.toString(), type);
            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            T t = ((Class<T>)type).newInstance();

//            StringReader in = new StringReader(data.toString());
            FileReader in = new FileReader(path);
            JsonReader reader = new JsonReader(in);
            String node;
            reader.beginObject();
            while(reader.hasNext()){
                node = reader.nextName();
                if ("data".equalsIgnoreCase(node)){
                    t.readFromJson(reader);
                }else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return t;

        } catch (Exception e) {
            throw new AppException(AppException.ErrorType.JSON,e.getMessage());
        }
    }
}
