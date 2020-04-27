package com.rv.justmeet.main.parser;

import org.json.JSONException;
import org.json.JSONObject;

import static com.rv.justmeet.utility.iOUtility.printer;

public class Parser {
    private static Parser instance = null;

    private Parser() {
    }

    public static Parser getInstance() {
        if (instance == null)
            instance = new Parser();
        return instance;
    }

    public boolean parseJsonResponse(String jsonString, String check) {

        return jsonString.contains(check);
    }

    public boolean parseSuccess(String jsonString) {
        try {
            JSONObject response = new JSONObject(jsonString);
            return response.getBoolean("success");
        } catch (JSONException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return false;
    }

    public String parseJsonresponseString(String jsonString) {
        try {
            JSONObject response = new JSONObject(jsonString);
            return response.getString("success");
        } catch (JSONException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return null;
    }
}
