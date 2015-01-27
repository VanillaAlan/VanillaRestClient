package com.hjbalan.vanillarest.api.tool;

import com.google.gson.Gson;

/**
 * Created by alan on 15/1/22.
 */
public class GsonHelper {

    private static Gson sGson;
    
    private GsonHelper() {
    }
    
    public static synchronized Gson getGsonInstance() {
        if (sGson == null) {
            sGson = new Gson();
        }
        return sGson;
    }
    
    public static <T extends Object> T getClassFromJsonString(String data, Class<T> clazz) {
        Gson gson = getGsonInstance();
        return gson.fromJson(data, clazz);
    }
    
}
