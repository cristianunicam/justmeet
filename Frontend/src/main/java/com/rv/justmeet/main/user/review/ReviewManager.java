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

import static com.rv.justmeet.utility.IOUtility.clearScreen;
import static com.rv.justmeet.utility.IOUtility.printer;

import java.util.HashMap;
import java.util.List;

/**
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 *
 * Classe Singleton che gestisce le azioni relative alle recensioni degli utenti
 */
public class ReviewManager implements ReviewManagerInterface{

    private static ReviewManager instance = null;
    private ReviewManager(){}

    public static ReviewManager getInstance(){
        if(instance == null)
            instance = new ReviewManager();
        return instance;
    }


    public void scriviRecensione(String emailUtente){
        HashMap<String, String> json = new HashMap<>();
        json.put("emailRecensore", LoggedUser.getInstance().getEmail());
        json.put("emailRecensito", emailUtente);
        json.put("voto", Integer.toString(IOUtility.inserisciInt("una valutazione per l'utente (da 0 a 5)" , 0 , 5 , "Inserire una valutazione tra 0 e 5!")));
        json.put("descrizione", IOUtility.inserisciStringa("un commento alla valutazione" , 0 , 500));

        Gson gson = new Gson();
        String response = BackendConnection.getInstance().checkAndRequest(
                getDomain()+"/inserimento", "POST", gson.toJson(json)
        );
        clearScreen();
        if(Parser.getInstance().parseSuccess(response))
            printer.accept("Recensione pubblicata!");
        else
            printer.accept("Errore nell'inserimento della recensione!");
    }


    public void visualizzaRecensioni(String emailUtente){
        String jsonString = BackendConnection.getInstance().checkAndRequest(
                getDomain()+"/visualizzarecensioni/"+ emailUtente, "GET",null
        );
        if(!jsonString.contains("emailRecensore")) {
            printer.accept("Nessuna recensione trovata!");
            return;
        }

        List<String> recensioni = ReviewParser.getInstance().parseRecensioni(jsonString);
        int cont = 0;
        for (String s : recensioni) {
            cont++;
            printer.accept(s);
            if(cont == 4){
                cont = 0;
                printer.accept("");
            }
        }
    }

    /**
     * Metodo che fornisce il path per le richieste rest relative alle recensioni
     *
     * @return il path relativo alle recensioni
     */
    private String getDomain(){ return "/recensioni";}
}
