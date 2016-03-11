package com.stay4it.http.itf;

import com.stay4it.http.error.AppException;

/**
 * Created by Stay on 12/7/15.
 * Powered by www.stay4it.com
 */
public interface OnGlobalExceptionListener {

    boolean handleException(AppException exception);
}
