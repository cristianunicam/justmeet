package com.rv.justmeet.main.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.rv.justmeet.utility.iOUtility.printer;

public class EventParser {

    private static EventParser instance = null;

    private EventParser() {
    }

    public static EventParser getInstance() {
        if (instance == null)
            instance = new EventParser();
        return instance;
    }

    public List<String> parseEvento(String jsonString) {
        String[] campi = {
                "id", "categoria", "titolo", "descrizione", "citta", "via", "data", "oraInizio", "oraFine", "prezzo", "maxPartecipanti", "emailOrganizzatore"
        };
        List<String> dati = new ArrayList<>();
        try {
            final JSONArray evento = new JSONArray(jsonString);
            JSONObject jsonObj = evento.getJSONObject(0);
            for (String s : campi)
                dati.add(jsonObj.getString(s));

        } catch (JSONException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return dati;

    }


    public List<String> parseBacheca(String jsonString) {
        String[] campi = {"id", "categoria", "titolo", "descrizione", "citta", "data", "prezzo"};
        List<String> dati = new ArrayList<>();
        try {
            final JSONArray bacheca = new JSONArray(jsonString);

            for (int i = 0; i < bacheca.length(); i++) {
                JSONObject jsonObj = bacheca.getJSONObject(i);
                for (String s : campi)
                    dati.add(jsonObj.getString(s));
            }
        } catch (JSONException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return dati;
    }

    public List<String> parseCategorie(String jsonString) {
        List<String> dati = new ArrayList<>();
        try {
            final JSONArray bacheca = new JSONArray(jsonString);

            for (int i = 0; i < bacheca.length(); i++) {
                JSONObject jsonObj = bacheca.getJSONObject(i);
                dati.add(jsonObj.getString("nome"));
            }
        } catch (JSONException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return dati;
    }
}
