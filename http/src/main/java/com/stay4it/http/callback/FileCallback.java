package com.stay4it.http.callback;

import com.stay4it.http.core.AbstractCallback;
import com.stay4it.http.error.AppException;

/**
 * Created by Stay on 4/7/15.
 * Powered by www.stay4it.com
 */
public abstract class FileCallback extends AbstractCallback<String> {

    @Override
    protected String bindData(String path) throws AppException {
        return path;
    }
}
