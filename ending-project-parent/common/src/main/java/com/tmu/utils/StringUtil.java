package com.tmu.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class StringUtil {


    public static boolean isNullOrEmpty(String input) {

        return input == null || input.trim().isEmpty();
    }

    public static boolean isNumberic(String sNumber) {
        if (sNumber == null || "".equals(sNumber)) {
            return false;
        }
        char ch_max = (char) 0x39;
        char ch_min = (char) 0x30;

        for (int i = 0; i < sNumber.length(); i++) {
            char ch = sNumber.charAt(i);
            if ((ch < ch_min) || (ch > ch_max)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isUUID(String string) {
        if (isNullOrEmpty(string)) {
            return false;
        }
        try {
            UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static Map<String, String> getUrlParamValues(String url) {
        Map<String, String> paramsMap = new HashMap();
        String[] params = url.split("&");
        String[] var4 = params;
        int var5 = params.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String param = var4[var6];
            String[] temp = param.split("=");

            try {
                paramsMap.put(temp[0], temp.length > 1 ? URLDecoder.decode(temp[1], "UTF-8") : "");
            } catch (UnsupportedEncodingException var9) {
                var9.printStackTrace();
            }
        }

        return paramsMap;
    }


    public static String generateMapString(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String result = builder.toString();
        return result != null && result.endsWith("&") ? result.substring(0, result.length() - 1) : result;
    }

    public static String printException(Exception ex) {
        return ex.getCause() != null ? ex.getCause().toString() : ex.toString();
    }
}
