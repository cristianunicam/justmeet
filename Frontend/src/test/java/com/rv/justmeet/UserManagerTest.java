package com.rv.justmeet;

import com.google.gson.Gson;
import com.rv.justmeet.main.parser.UserParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;

import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.utility.RequestComunication;

import static org.junit.jupiter.api.Assertions.*;


class UserManagerTest {

    @AfterEach
    public void deleteRegisteredUserForTest(){
        RequestComunication.getInstance().restRequest("/utente/eliminatest", "GET",null);
    }


    @Test
    public void RegistrationTest(){
        HashMap<String, String> json = new HashMap<>();
        json.put("email", "test@unicam.it");
        json.put("password", "password");
        json.put("nome", "nome");
        json.put("cognome", "cognome");
        json.put("eta", "20");

        Gson gson = new Gson();
        String response = RequestComunication.getInstance().restRequest("/utente/registrazione", "POST", gson.toJson(json));
        assertTrue(Parser.getInstance().parseSuccess(response));
    }


    @Test
    public void LoginTestShouldBeTrue(){
        inserisciUtenteTest();
        String response = RequestComunication.getInstance().restRequest("/utente/login", "POST",
                "{ \"email\":\"" +  "test@unicam.it" + "\", \"password\":\"" + "password" + "\"}");
        assertTrue(Parser.getInstance().parseSuccess(response));
    }


    @Test
    public void LoginTestShouldBeFalse(){
        inserisciUtenteTest();
        String response = RequestComunication.getInstance().restRequest("/utente/login", "POST",
                "{ \"email\":\"aaaaaaaaaaa\", \"password\":\"bbbbbbbbbbb\"}");
        assertFalse(Parser.getInstance().parseSuccess(response));
    }


    @Test
    public void ModicaUtenteShouldBeTrue(){
        inserisciUtenteTest();
        RequestComunication.getInstance().restRequest(
                "/utente/modifica/test@unicam.it:eta:14" ,"GET",null);
        String risposta = RequestComunication.getInstance().restRequest(
                "/utente/getprofilo/test@unicam.it","GET",null);
        List<String> datiProfilo = UserParser.getInstance().parseDatiUtente(risposta);
        assertEquals("eta: 14" , datiProfilo.get(3));
    }


    private void inserisciUtenteTest(){
        HashMap<String, String> json = new HashMap<>();
        json.put("email", "test@unicam.it");
        json.put("password", "password");
        json.put("nome", "nome");
        json.put("cognome", "cognome");
        json.put("eta", "20");

        Gson gson = new Gson();
        RequestComunication.getInstance().restRequest("/utente/registrazione", "POST", gson.toJson(json));
    }
}
