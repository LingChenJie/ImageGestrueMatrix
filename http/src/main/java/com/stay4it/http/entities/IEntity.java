package com.stay4it.http.entities;

import com.google.gson.stream.JsonReader;
import com.stay4it.http.error.AppException;

/**
 * Created by Stay on 15/7/15.
 * Powered by www.stay4it.com
 */
public interface IEntity {
    void readFromJson(JsonReader reader) throws AppException;
}
