package com.rv.justmeet;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.utility.RequestComunication;

import static org.junit.jupiter.api.Assertions.*;


class UserManagerTest {

    @AfterAll
    public static void deleteRegisteredUserForTest(){
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
        String response = RequestComunication.getInstance().restRequest("/utente/login", "POST",
                "{ \"email\":\"" +  "test@unicam.it" + "\", \"password\":\"" + "password" + "\"}");
        assertTrue(Parser.getInstance().parseSuccess(response));
    }

    @Test
    public void LoginTestShouldBeFalse(){
        String response = RequestComunication.getInstance().restRequest("/utente/login", "POST",
                "{ \"email\":\"aaaaaaaaaaa\", \"password\":\"bbbbbbbbbbb\"}");
        assertFalse(Parser.getInstance().parseSuccess(response));
    }
}
