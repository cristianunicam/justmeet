package com.rv.justmeet;

import com.google.gson.Gson;
import com.rv.justmeet.main.parser.EventParser;
import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.utility.RequestComunication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class EventManagerTest {


    @BeforeAll
    public static void creaUtenteTest(){
        HashMap<String, String> json = new HashMap<>();
        json.put("email", "test@unicam.it");
        json.put("password", "password");
        json.put("nome", "nome");
        json.put("cognome", "cognome");
        json.put("eta", "20");

        Gson gson = new Gson();
        RequestComunication.getInstance().restRequest("/utente/registrazione", "POST", gson.toJson(json));
    }

    @AfterAll
    public static void cancellaUtentetest(){
        RequestComunication.getInstance().restRequest("/utente/eliminatest", "GET",null);
    }

    @Test
    public void pubblicaEventoTest(){
        String response = pubblicaEvento();
        assertTrue(Parser.getInstance().parseSuccess(response));
        annulla();
    }


    @Test
    public void partecipaAdEventoTest(){
        pubblicaEvento();
        String response = RequestComunication.getInstance().restRequest(
               "/eventi/geteventipubblicati/test@unicam.it", "GET",null);
        List<String> eventiPubblicati = EventParser.getInstance().parseBacheca(response);
        String response2 = RequestComunication.getInstance().restRequest(
                "/utente/partecipa/test@unicam.it:" + eventiPubblicati.get(0), "GET",null);
        assertEquals("true" , Parser.getInstance().parseJsonResponseString(response2));
        annulla();
    }


    @Test
    public void partecipaAdEventoGiaPartecipato(){
        pubblicaEvento();
        String response = RequestComunication.getInstance().restRequest(
                "/eventi/geteventipubblicati/test@unicam.it", "GET",null);
        List<String> eventiPubblicati = EventParser.getInstance().parseBacheca(response);
        RequestComunication.getInstance().restRequest(
                "/utente/partecipa/test@unicam.it:" + eventiPubblicati.get(0), "GET",null);
        String response3 = RequestComunication.getInstance().restRequest(
                "/utente/partecipa/test@unicam.it:" + eventiPubblicati.get(0), "GET",null);
        assertEquals("presente", Parser.getInstance().parseJsonResponseString(response3));
        annulla();
    }


    @Test
    public void annullaPartecipazioneTest(){
        pubblicaEvento();
        String response = RequestComunication.getInstance().restRequest(
                "/eventi/geteventipubblicati/test@unicam.it", "GET",null);
        List<String> eventiPubblicati = EventParser.getInstance().parseBacheca(response);
        RequestComunication.getInstance().restRequest(
                "/utente/partecipa/test@unicam.it:" + eventiPubblicati.get(0), "GET",null);
        String response2 = RequestComunication.getInstance().restRequest(
                "/utente/annullapartecipazione/test@unicam.it:" + eventiPubblicati.get(0), "GET",null);
        assertEquals("true" , Parser.getInstance().parseJsonResponseString(response2));
        annulla();
    }

    @Test
    public void annullaEvento(){
        pubblicaEvento();
        String response = annulla();
        assertTrue(Parser.getInstance().parseSuccess(response));
    }


    @Test
    public void partecipaAdEventoAlCompleto(){
        HashMap<String, String> json = new HashMap<>();
        json.put("categoria", "1");
        json.put("titolo", "titoloooooooo");
        json.put("descrizione", "descrizioneeeeeeeeeeeeee");
        json.put("citta", "citta");
        json.put("via", "viaaaa");
        json.put("data", "2020-12-20");
        json.put("oraInizio", "11:30");
        json.put("oraFine", "12:00");
        json.put("prezzo", "11.30");
        json.put("maxPartecipanti", "0");
        json.put("emailOrganizzatore", "test@unicam.it");
        Gson gson = new Gson();
        RequestComunication.getInstance().restRequest("/eventi/inserimento", "POST", gson.toJson(json));

        String response2 = RequestComunication.getInstance().restRequest(
                "/eventi/geteventipubblicati/test@unicam.it", "GET",null);
        List<String> eventiPubblicati = EventParser.getInstance().parseBacheca(response2);
        String response3 = RequestComunication.getInstance().restRequest(
                "/utente/partecipa/test@unicam.it:" + eventiPubblicati.get(0), "GET",null);
        assertEquals("pieno", Parser.getInstance().parseJsonResponseString(response3));
        annulla();
    }

    @Test
    public void modificaEventoTest(){
        pubblicaEvento();
        String response2 = RequestComunication.getInstance().restRequest(
                "/eventi/geteventipubblicati/test@unicam.it", "GET",null);
        List<String> eventiPubblicati = EventParser.getInstance().parseBacheca(response2);
        HashMap<String, String> json = new HashMap<>();
        json.put("nomeCampo", "titolo");
        json.put("campoModificato", "\"nuovotitolomodificato\"");
        json.put("idEvento", eventiPubblicati.get(0));
        Gson gson = new Gson();
        String response = RequestComunication.getInstance().restRequest("/eventi/modifica", "POST", gson.toJson(json));

        assertTrue(Parser.getInstance().parseSuccess(response));
        annulla();
    }


    private String pubblicaEvento(){
        HashMap<String, String> json = new HashMap<>();
        json.put("categoria", "1");
        json.put("titolo", "titoloooooooo");
        json.put("descrizione", "descrizioneeeeeeeeeeeeee");
        json.put("citta", "citta");
        json.put("via", "viaaaa");
        json.put("data", "2020-12-20");
        json.put("oraInizio", "11:30");
        json.put("oraFine", "12:00");
        json.put("prezzo", "11.30");
        json.put("maxPartecipanti", "20");
        json.put("emailOrganizzatore", "test@unicam.it");

        Gson gson = new Gson();
        return RequestComunication.getInstance().restRequest("/eventi/inserimento", "POST", gson.toJson(json));
    }

    private String annulla() {
        String response2 = RequestComunication.getInstance().restRequest(
                "/eventi/geteventipubblicati/test@unicam.it", "GET",null);
        List<String> eventiPubblicati = EventParser.getInstance().parseBacheca(response2);
        return RequestComunication.getInstance().restRequest(
                "/eventi/annulla/test@unicam.it:" + eventiPubblicati.get(0), "GET",null
        );
    }

}
