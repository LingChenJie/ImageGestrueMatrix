package com.stay4it.http.core;

import com.stay4it.http.error.AppException;
import com.stay4it.http.Request;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Stay
 * @version create time：Sep 15, 2014 11:42:11 AM
 */
public class HttpClientUtil {
    private static HttpClient client;


    public static HttpResponse execute(Request request) throws AppException {
        switch (request.method) {
            case GET:
                return get(request);
            case POST:
                return post(request);
            case DELETE:

                break;
            case PUT:

                break;
            default:
                throw new AppException(AppException.ErrorType.MANUAL, "the request method " + request.method.name() + " can't be supported");
        }
        return null;
    }

    private static HttpResponse get(Request request) throws AppException {
        try {
            HttpClient client = getHttpClient();
            HttpGet get = new HttpGet(request.url);
            addHeader(get, request.headers);
            return client.execute(get);
        } catch (ConnectTimeoutException e) {
            throw new AppException(AppException.ErrorType.TIMEOUT, e.getMessage());
        } catch (ClientProtocolException e) {
            throw new AppException(AppException.ErrorType.SERVER, e.getMessage());
        } catch (IOException e) {
            throw new AppException(AppException.ErrorType.SERVER, e.getMessage());
        }
    }

    private static HttpResponse post(Request request) throws AppException {
        try {
            HttpClient client = getHttpClient();
            HttpPost post = new HttpPost(request.url);

            addHeader(post, request.headers);

            if (request.content == null) {
                throw new AppException(AppException.ErrorType.MANUAL, "you should set the value of the post content");
            }

            StringEntity entity = new StringEntity(request.content);
//            here you can also expand to @UrlEncodedFormEntity || @ByteArrayEntity
            post.setEntity(entity);
            return client.execute(post);
        } catch (ConnectTimeoutException e) {
            throw new AppException(AppException.ErrorType.TIMEOUT, e.getMessage());
        } catch (ClientProtocolException e) {
            throw new AppException(AppException.ErrorType.SERVER, e.getMessage());
        } catch (IOException e) {
            throw new AppException(AppException.ErrorType.SERVER, e.getMessage());
        }
    }

    public static synchronized HttpClient getHttpClient() {

        if (null == client) {
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,
                    "UTF-8");
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams
                    .setUserAgent(
                            params,
                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                    + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            ConnManagerParams.setTimeout(params, 1000);
            HttpConnectionParams.setConnectionTimeout(params, 2000);
            HttpConnectionParams.setSoTimeout(params, 4000);

            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));

            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);
            client = new DefaultHttpClient(conMgr, params);
        }

        return client;
    }


    private static void addHeader(HttpUriRequest mHttpUriRequest, Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            for (Entry<String, String> entry : headers.entrySet()) {
                mHttpUriRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }
}
