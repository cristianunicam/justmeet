package com.rv.justmeet.main.event;

import com.rv.justmeet.exceptions.*;
import com.rv.justmeet.main.controller.EventController;
import com.rv.justmeet.main.core.MySQLConnection;
import com.rv.justmeet.main.core.SoftwareManager;
import com.rv.justmeet.main.user.LoggedUser;
import com.rv.justmeet.utility.iOUtility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.rv.justmeet.main.core.SoftwareManager.printer;
import static com.rv.justmeet.main.core.SoftwareManager.scanner;


/**
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 *
 * Classe Singleton che gestisce le azioni relaive agli eventi
 */
public class EventsManager{
    private Map<String,Supplier<?>> campiEvento = new HashMap<>();

    private static EventsManager instance = null;

    private EventsManager(){}

    public static EventsManager getInstance(){
        if(instance == null) {
            instance = new EventsManager();
            instance.mapInit();
        }
        return instance;
    }

    private void mapInit(){
        campiEvento.put("titolo",() -> iOUtility.inserisciStringa("titolo",10,50));
        campiEvento.put("descrizione", () -> iOUtility.inserisciStringa("descrizione",15,500));
        campiEvento.put("citta", () -> iOUtility.inserisciStringa("citta",3,30));
        campiEvento.put("via",() -> iOUtility.inserisciStringa("via",4,30));
        campiEvento.put("data", () -> iOUtility.inserisciData("Inserisci la data dell'evento nella forma AAAA-MM-DD: "));
        campiEvento.put("oraInizio", () -> iOUtility.inserisciOra("inizio"));
        campiEvento.put("oraFine", () -> iOUtility.inserisciOra("inizio"));
        campiEvento.put("prezzo",() -> iOUtility.inserisciFloat("prezzo"));
        campiEvento.put("maxPartecipanti", () -> iOUtility.inserisciInt(
                           "numero massimo partecipanti",2,100000,"Il numero di partecipanti deve essere compreso tra 2 e 100000" ));
    }

    /**
     * Permette di creare un evento che verra' inserito in bacheca e salvato nel database
     */
    public void aggiungiEvento(){
        final int categoria = iOUtility.inserisciCategoriaEvento();
        final String titolo = (String) campiEvento.get("titolo").get();
        final String descrizione = (String) campiEvento.get("descrizione").get();
        final String citta = (String) campiEvento.get("citta").get();
        final String via = (String) campiEvento.get("via").get();
        final String data = (String) campiEvento.get("data").get();
        final String oraInizio = (String) campiEvento.get("oraInizio").get();
        final String oraFine = (String) campiEvento.get("oraFine").get();
        final Float prezzo = (Float) campiEvento.get("prezzo").get();
        final Integer maxPartecipanti =(Integer) campiEvento.get("maxPartecipanti").get();

        EventController.aggiungiEvento(categoria,titolo,descrizione,citta,via,data,oraInizio,oraFine,prezzo,maxPartecipanti,LoggedUser.getInstance().getEmail());
  }


    /**
     * Permette di scegliere un evento a cui partecipare. Salvando poi la partecipazione nel database
     */
    public void partecipaEvento(final int idEvento){
        if(!EventController.partecipaEvento(idEvento))
            printer.accept("Errore, partecipazione non avvenuta!");
        printer.accept("Partecipazione effettuata!");
    }


    /**
     * Metodo per annullare la partecipazione ad un evento
     * @param idEvento id dell'evento del quale si vuole annullare la partecipazione
     */
    public void annullaPartecipazione(final int idEvento){
        if(EventController.annullaPartecipazione(idEvento))
            printer.accept("Eliminazione effettutata");
        else
            printer.accept("Eliminazione non effettuata");
    }

    /**
     * Metodo per la modifica di un evento
     *
     * @param idEvento id dell'evento che si vuole modificare
     */
    public void modificaEvento(int idEvento){
        //Mostra tutti i campi modificabili
        printer.accept("0) Per annullare");
        for(int x = 0; x < EventController.campiEventoModificabili.length ; x++)
            printer.accept(x+1+") "+EventController.campiEventoModificabili[x]);
        //Richiede l'inserimento del campo da voler modificare
        printer.accept("Inserisci il numero del campo da voler modificare: ");
        int campoDaModificare = scanner.nextInt();
        if(campoDaModificare == 0) return;

        //Informazioni del campo modificato
        printer.accept("Inserisci le nuove informazioni del campo: ");
        String campoModificato;

        if(campoDaModificare < EventController.campiDatabaseModificabili.length+1)
            campoModificato = "\""+campiEvento.get(EventController.campiDatabaseModificabili[campoDaModificare-1]).get()+"\"";
        else{
            printer.accept("Errore nell'inserimento del campo da moficare.");
            return;
        }

        EventController.modificaEvento(
                EventController.campiDatabaseModificabili[campoDaModificare-1],
                campoModificato,
                idEvento
        );
    }


    /**
     * Metodo per annullare un evento
     */
    public void annullaEvento(final int idEvento){
        EventController.annullaEvento(LoggedUser.getInstance().getEmail(),idEvento);
        printer.accept("Evento annullato!");
    }
}