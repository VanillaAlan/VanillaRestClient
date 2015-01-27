package com.hjbalan.vanillarest.api.tool;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.hjbalan.vanillarest.Config;
import com.hjbalan.vanillarest.Util.NetWorkUtils;
import com.hjbalan.vanillarest.volley.StringMultipartRequest;
import com.hjbalan.vanillarest.volley.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * used to request api with multi part,the method is post
 *
 * @author alan
 */
public abstract class BaseMultiApiRequest<Params, Result> implements
        Listener<String>, ErrorListener {

    public static final String TAG = "BaseMultiApiRequest";

    protected ApiResponseListener.ResultListener<Result> mListener;

    protected ApiResponseListener.ApiErrorListener mErrorListener;

    protected Context ctx;

    protected StringMultipartRequest mRequest;

    protected Params mParams;

    protected String mTag;

    public BaseMultiApiRequest(Context ctx) {
        this(ctx, null, null);
    }

    public BaseMultiApiRequest(Context ctx, ApiResponseListener.ResultListener<Result> listener,
            ApiResponseListener.ApiErrorListener errorListener) {
        this.ctx = ctx;
        String address = buildAddress();
        mRequest = new StringMultipartRequest(address, this, this);
        int timeout = NetWorkUtils.isConnectedWifi(ctx) ? Config.CONNECT_TIMEOUT_WIFI
                : Config.CONNECT_TIMEOUT_MOBILE;
        mRequest.setRetryPolicy(new DefaultRetryPolicy(timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mListener = listener;
        mErrorListener = errorListener;

    }

//    TODO:support switch host
    private String buildAddress() {
//        if (SharePref.isForeignUser(ctx)) {
//            return Constants.HOST_Foreign + bindApiPath();
//        } else {
//            return Constants.HOST + bindApiPath();
//        }
        return Config.HOST + bindApiPath();
    }

    /**
     * 拼接API接口地址
     */
    protected abstract String bindApiPath();

    @Override
    public void onErrorResponse(VolleyError error) {
        handleErrorResponse(HandleErrorHelper.handleVolleyError(ctx, error));
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            int code = jsonResponse.optInt("ret", -1);
            if (code == 0) {
                handleCorrectResponse(jsonResponse);
            } else {
                String errorMessage = jsonResponse.optString("error");
                handleErrorResponse(HandleErrorHelper.handleApiError(code,
                        errorMessage));
            }
        } catch (JSONException e) {
            handleErrorResponse(HandleErrorHelper.handleApiError(2005, ""));
            e.printStackTrace();
        }
    }

    /**
     * handle jsonResponse when return code is 0
     */
    protected void handleCorrectResponse(JSONObject jsonResponse) {
        Result result = parseJsonToResult(jsonResponse);
        if (mListener != null) {
            mListener.onResult(result);
        }
    }

    /**
     * parse JSONObject response to result
     */
    protected abstract Result parseJsonToResult(JSONObject jsonResponse);

    /**
     * handle error when onErrorResponse is called or return code is not 0
     */
    protected void handleErrorResponse(ApiError error) {
        if (mErrorListener != null) {
            mErrorListener.onError(mTag, error);
        }
    }

    /**
     * post params
     */
    protected abstract void postParams(Map<String, String> params);

    /**
     * set tag for request
     */
    public BaseMultiApiRequest<Params, Result> setRequestTag(String tag) {
        mTag = tag;
        mRequest.setTag(tag);
        return this;
    }

    /**
     * add current request to queue
     */
    public void execute() {
        addMultiPartParams();
        VolleyManager.getInstance().addToRequestQueue(mRequest, mTag);
    }

    /**
     * build params for request
     */
    public BaseMultiApiRequest<Params, Result> buildParams(Params params) {
        this.mParams = params;
        return this;
    }

    private void addMultiPartParams() {
        Map<String, String> params = getParams();
        for (String key : params.keySet()) {
            mRequest.addMultipartParam(key, params.get(key));
        }
    }

    //TODO:get access token
    private Map<String, String> getParams() {
        String accessT = "";
        Map<String, String> params = new TreeMap<String, String>();

//        UserInfo info = User.getUserInfo(ctx);
//        if (info != null) {
//            accessT = info.accessToken;
//        }
        Map<String, String> headers = new TreeMap<String, String>();
        ApiUtils.buildDefaultHearders(headers);
        if (!TextUtils.isEmpty(accessT)) {
            headers.put("Authorization", "Basic " + accessT);
        }
        mRequest.setHeaders(headers);
        postParams(params);
        return params;
    }

    /**
     * add file to upload
     */
    public BaseMultiApiRequest<Params, Result> buildFileParams(String fileName,
            String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            mRequest.addFile(fileName, filePath);
        }
        return this;
    }

}
