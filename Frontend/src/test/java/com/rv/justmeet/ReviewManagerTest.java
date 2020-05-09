package com.rv.justmeet;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.utility.RequestComunication;




public class ReviewManagerTest {


    @BeforeAll
    public static void creazioneUtentiDiTest(){
        HashMap<String, String> json = new HashMap<>();
        json.put("email", "test1@unicam.it");
        json.put("password", "password");
        json.put("nome", "nome");
        json.put("cognome", "cognome");
        json.put("eta", "20");

        Gson gson = new Gson();
        RequestComunication.getInstance().restRequest("/utente/registrazione", "POST", gson.toJson(json));


        HashMap<String, String> json2 = new HashMap<>();
        json2.put("email", "test2@unicam.it");
        json2.put("password", "password");
        json2.put("nome", "nome");
        json2.put("cognome", "cognome");
        json2.put("eta", "20");

        Gson gson2 = new Gson();
        RequestComunication.getInstance().restRequest("/utente/registrazione", "POST", gson2.toJson(json2));
    }

    @AfterAll
    public static void cancellazioneUtentiDiTest(){
        RequestComunication.getInstance().restRequest("/utente/eliminatest2", "GET",null);
    }

    @Test
    public void inserimentoRecensioneTest(){
        HashMap<String, String> json = new HashMap<>();
        json.put("emailRecensore", "test1@unicam.it");
        json.put("emailRecensito", "test2@unicam.it");
        json.put("voto", "5");
        json.put("descrizione", "Questa e una stringa di test");

        Gson gson = new Gson();
        String response = RequestComunication.getInstance().restRequest(
                "/recensioni/inserimento", "POST", gson.toJson(json)
        );

        assertTrue(Parser.getInstance().parseSuccess(response));
    }
}
