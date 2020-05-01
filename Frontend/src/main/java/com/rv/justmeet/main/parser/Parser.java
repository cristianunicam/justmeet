package com.rv.justmeet.main.parser;

import org.json.JSONException;
import org.json.JSONObject;

import static com.rv.justmeet.utility.IOUtility.printer;

public class Parser {
    private static Parser instance = null;

    private Parser() {
    }

    public static Parser getInstance() {
        if (instance == null)
            instance = new Parser();
        return instance;
    }

    /**
     * Controlla che una stringa json contenga un determinato campo
     *
     * @param jsonString la stringa da controllare
     * @param check il campo del quale controllare la presenza all'interno del codice
     * @return <code>true</code> se è presente, <code>false</code> altrimenti
     */
    public boolean parseJsonResponse(String jsonString, String check) {
        return jsonString.contains(check);
    }


    /**
     * Effettua il parse della stringa passata e ritorna il valore
     *
     * @param jsonString la stringa della quale effettuare il parse
     * @return <code>true</code> se al campo json "success" è stato
     *         assegnato tale valore, <code>false</code> altrimenti
     */
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


    /**
     * Effettua il parse di una determinata stringa e ne restituisce il valore corrispondente
     * al campo "success"
     *
     * @param jsonString stringa json contente i dati
     * @return Stringa contente il valore corrispondente al campo "success"
     */
    public String parseJsonResponseString(String jsonString) {
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
