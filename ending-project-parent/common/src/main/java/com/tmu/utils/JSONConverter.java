package com.tmu.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class JSONConverter {


        private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        public static String toJSON(Object obj) {
            return GSON.toJson(obj);
        }

        public static <T> T toObject(String value, Class<T> actualObject) {
            try {
                return GSON.fromJson(value, actualObject);
            } catch (JsonSyntaxException ex) {
                return null;
            }
        }

    }
