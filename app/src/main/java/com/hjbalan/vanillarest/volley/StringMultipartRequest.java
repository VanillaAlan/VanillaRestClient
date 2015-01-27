package com.hjbalan.vanillarest.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

public class StringMultipartRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();

    private Map<String, String> mHeaders;

    private final Response.Listener<String> mListener;

    public StringMultipartRequest(String url, Response.ErrorListener errorListener,
            Response.Listener<String> listener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
    }

    public void addFile(String fileName, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (file == null || !file.isFile()) {
            return;
        }
        entity.addPart(fileName, new FileBody(file));
    }

    public void addMultipartParam(String name, String value) {
        try {
            entity.addPart(name,
                    new StringBody(value, Charset.forName("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    public void setHeaders(Map<String, String> map) {
        this.mHeaders = map;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mHeaders == null || mHeaders.isEmpty()) {
            return Collections.emptyMap();
        }
        return mHeaders;
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}