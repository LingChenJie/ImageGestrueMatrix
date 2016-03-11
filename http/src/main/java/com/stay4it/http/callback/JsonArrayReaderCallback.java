package com.stay4it.http.callback;

import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.stay4it.http.core.AbstractCallback;
import com.stay4it.http.error.AppException;
import com.stay4it.http.entities.IEntity;

import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Stay on 4/7/15.
 * Powered by www.stay4it.com
 */
public abstract class JsonArrayReaderCallback<T extends IEntity> extends AbstractCallback<ArrayList<T>> {
    @Override
    protected ArrayList<T> bindData(String path) throws AppException {
        try {

            Log.e("stay", "JsonReaderCallback bindData:" + path);
//            JSONObject json = new JSONObject(result);
//            JSONObject data = json.optJSONObject("data");
//            Gson gson = new Gson();
//            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//            return gson.fromJson(data.toString(), type);
            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            ArrayList<T> ts = new ArrayList<T>();
            T t  ;

//            StringReader in = new StringReader(data.toString());
            FileReader in = new FileReader(path);
            JsonReader reader = new JsonReader(in);
            String node;
            reader.beginObject();
            while(reader.hasNext()){
                node = reader.nextName();
                if ("data".equalsIgnoreCase(node)){
                    reader.beginArray();
                    while(reader.hasNext()){
                        t = ((Class<T>)type).newInstance();
                        t.readFromJson(reader);
                        ts.add(t);
                    }
                    reader.endArray();
                }else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return ts;

        } catch (Exception e) {
            throw new AppException(AppException.ErrorType.JSON,e.getMessage());
        }
    }
}
