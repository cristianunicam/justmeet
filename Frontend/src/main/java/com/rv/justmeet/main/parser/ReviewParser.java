package com.rv.justmeet.main.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.rv.justmeet.utility.IOUtility.printer;

public class ReviewParser {
    private static ReviewParser instance = null;

    public static ReviewParser getInstance() {
        if (instance == null)
            instance = new ReviewParser();
        return instance;
    }

    public List<String> parseRecensioni(String jsonString){
        String[] campi = {"emailRecensore" , "emailRecensito" , "voto" , "descrizione"};

        List<String> dati = new ArrayList<>();
        try {
            final JSONArray evento = new JSONArray(jsonString);
            JSONObject jsonObj = evento.getJSONObject(0);
            for(int x = 0 ; x < evento.length() ; x++) {
                for (String s : campi)
                    dati.add(s + ": " + jsonObj.getString(s));
            }
        } catch (JSONException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return dati;
    }
}