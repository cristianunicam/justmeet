package com.rv.justmeet.main.user.review;

import com.google.gson.Gson;
import com.rv.justmeet.main.core.BackendConnection;
import com.rv.justmeet.main.parser.EventParser;
import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.main.parser.ReviewParser;
import com.rv.justmeet.main.parser.UserParser;
import com.rv.justmeet.main.user.LoggedUser;
import com.rv.justmeet.utility.IOUtility;
import com.rv.justmeet.utility.RequestComunication;

import static com.rv.justmeet.utility.IOUtility.printer;

import java.util.HashMap;
import java.util.List;

public class ReviewManager {


    public static void scriviRecensione(String emailUtente){
        HashMap<String, String> json = new HashMap<>();
        json.put("emailRecensore", LoggedUser.getInstance().getEmail());
        json.put("emailRecensito", emailUtente);
        json.put("voto", Integer.toString(IOUtility.inserisciInt("una valutazione per l'utente (da 0 a 5)" , 0 , 5 , "Inserire una valutazione tra 0 e 5!")));
        json.put("descrizione", IOUtility.inserisciStringa("un commento alla valutazione" , 0 , 500));

        Gson gson = new Gson();
        String response = RequestComunication.getInstance().restRequest(
                getDomain()+"/inserimento", "POST", gson.toJson(json)
        );

        if(Parser.getInstance().parseSuccess(response))
            printer.accept("Recensione pubblicata!");
        else
            printer.accept("Errore nell'inserimento della recensione!");
    }



    public static void visualizzaRecensioni(String emailUtente){
        String jsonString = BackendConnection.getInstance().checkAndRequest(
                getDomain()+"/visualizzarecensioni/"+ emailUtente, "GET",null
        );
        List<String> recensioni = ReviewParser.getInstance().parseRecensioni(jsonString);
        if(recensioni.size() == 0) {
            printer.accept("Nessuna recensione trovata!");
            return;
        }
        //alla fine di una recensione mette una riga vuota
        for (String s : recensioni) {
            for (int y = 0; y < 4; y++)
                printer.accept(s);
            printer.accept("");
        }
    }

    private static String getDomain(){ return "/recensioni";}

}
