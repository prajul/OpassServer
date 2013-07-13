package com.opassserver;

import android.content.Context;

import com.loopj.android.http.*;

import org.apache.http.HttpEntity;

/**
 * Created by Prajul on 26/6/13.
 */
public class RestClient {
    private static final String BASE_URL = "http://10.0.2.2/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public  static  void post (Context context,String url, HttpEntity entity,String type, AsyncHttpResponseHandler handler) {
        client.post(null,getAbsoluteUrl(url),entity,type,handler);
    }
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
