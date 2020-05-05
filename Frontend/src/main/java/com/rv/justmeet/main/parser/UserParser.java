package com.rv.justmeet.main.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rv.justmeet.utility.IOUtility.printer;

/**
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 */
public class UserParser {
    private static UserParser instance = null;

    public static UserParser getInstance() {
        if (instance == null)
            instance = new UserParser();
        return instance;
    }

    /**
     * Effettua il parse dei dati di un utente
     *
     * @param jsonString stringa in formato json da cui effettuare il parse
     * @return la lista dei dati dell'utente
     */
    public List<String> parseDatiUtente(String jsonString){
        String[] campi = {"email" , "nome" , "cognome" , "eta"};
        List<String> dati = new ArrayList<>();
        try {
            final JSONArray evento = new JSONArray(jsonString);
            JSONObject jsonObj = evento.getJSONObject(0);
            for (String s : campi)
                dati.add(s+": "+jsonObj.getString(s));

        } catch (JSONException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return dati;
    }


    /**
     * Effettua il parse delle email degli utenti partecipanti ad un evento
     *
     * @param jsonString stringa in formato json da cui effettuare il parse
     * @return Map contenente le email degli utenti
     */
    public Map<Integer , String> parsePartecipanti(String jsonString){
        Map<Integer , String> partecipanti = new HashMap<>();
        try {
            final JSONArray jsonUtenti = new JSONArray(jsonString);
            JSONObject jsonObj;
            for(int x = 0 ; x < jsonUtenti.length() ; x++) {
                jsonObj = jsonUtenti.getJSONObject(0);
                partecipanti.put(x,jsonObj.getString("email"));
            }
        } catch (JSONException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return partecipanti;
    }

}
