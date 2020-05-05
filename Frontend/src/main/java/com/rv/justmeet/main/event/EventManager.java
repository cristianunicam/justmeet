package com.rv.justmeet.main.event;

import com.google.gson.Gson;
import com.rv.justmeet.exceptions.FieldToModifyDoesNotExistsException;
import com.rv.justmeet.main.core.BackendConnection;
import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.main.user.LoggedUser;
import com.rv.justmeet.utility.RequestComunication;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.rv.justmeet.utility.IOUtility.*;


/**
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 * <p>
 * Classe che gestisce le azioni relative agli eventi
 */
public class EventManager implements EventManagerInterface{
    private static EventManager instance = null;
    private final Map<String, Supplier<?>> campiEvento = new HashMap<>();
    private final String[] campiDatabaseModificabili = {
            "titolo", "descrizione", "citta", "via", "data", "oraInizio", "oraFine", "prezzo","minPartecipanti", "maxPartecipanti"
    };
    private final String[] campiEventoModificabili = {
            "Titolo", "Descrizione", "Citta'", "Via", "Data", "Ora inizio", "Ora fine", "Prezzo", "Numero minimo partecipanti","Numero massimo partecipanti"
    };

    private EventManager() {}


    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
            instance.mapInit();
        }
        return instance;
    }


    /**
     * Metodo che fornisce il path per le richieste rest relative agli eventi
     *
     * @return il path relativo agli eventi
     */
    private static String getDomain() {
        return "/eventi";
    }


    /**
     * Inizializza l'HashMap che conterrà i metodi per l'inserimento dei dati di un evento
     */
    private void mapInit() {
        campiEvento.put("titolo", () -> inserisciStringa("il titolo", 10, 50));
        campiEvento.put("descrizione", () -> inserisciStringa("la descrizione", 15, 500));
        campiEvento.put("citta", () -> inserisciStringa("la citta", 3, 30));
        campiEvento.put("via", () -> inserisciStringa("la via", 4, 30));
        campiEvento.put("data", () -> inserisciData("Inserisci la data dell'evento nella forma AAAA-MM-DD: "));
        campiEvento.put("oraInizio", () -> inserisciOra("inizio"));
        campiEvento.put("oraFine", () -> inserisciOra("fine"));
        campiEvento.put("prezzo", () -> inserisciFloat("prezzo"));
        campiEvento.put("minPartecipanti", () -> inserisciInt(
                "numero minimo di partecipanti affinche' l'evento possa esserci",0, 50000,"Il numero minimo di partecipanti deve essere maggiore di 0"
        ));
        campiEvento.put("maxPartecipanti", () -> inserisciInt(
                "numero massimo partecipanti", 2, 100000, "Il numero di partecipanti deve essere compreso tra 2 e 100000"));
    }


    public void aggiungiEvento() {
        HashMap<String, String> json = new HashMap<>();
        json.put("categoria", Integer.toString(inserisciCategoriaEvento()));
        json.put("titolo", (String) campiEvento.get("titolo").get());
        json.put("descrizione", (String) campiEvento.get("descrizione").get());
        json.put("citta", (String) campiEvento.get("citta").get());
        json.put("via", (String) campiEvento.get("via").get());
        json.put("data", (String) campiEvento.get("data").get());
        json.put("oraInizio", (String) campiEvento.get("oraInizio").get());
        json.put("oraFine", (String) campiEvento.get("oraFine").get());
        json.put("prezzo", campiEvento.get("prezzo").get().toString());
        json.put("minPartecipanti",campiEvento.get("minPartecipanti").get().toString());
        json.put("maxPartecipanti", campiEvento.get("maxPartecipanti").get().toString());
        json.put("emailOrganizzatore", LoggedUser.getInstance().getEmail());
        Gson gson = new Gson();
        String response = RequestComunication.getInstance().restRequest(
                getDomain() + "/inserimento", "POST", gson.toJson(json)
        );

        if (Parser.getInstance().parseSuccess(response))
            printer.accept("Evento inserito!");
        else
            printer.accept("Evento non inserito!");
    }


    public void modificaEvento(int idEvento) {
        String campoModificato;
        //Mostra tutti i campi modificabili
        printer.accept("0) Per annullare");
        for (int x = 0; x < campiEventoModificabili.length; x++)
            printer.accept(x + 1 + ") " + campiEventoModificabili[x]);

        //Richiede l'inserimento del campo da voler modificare
        printer.accept("Inserisci il numero del campo da voler modificare: ");
        int campoDaModificare = scanner.nextInt();
        String nomeCampo;
        try {
            if (campoDaModificare == 0)
                return;
            else if (campoDaModificare < campiDatabaseModificabili.length + 1) {
                nomeCampo = campiDatabaseModificabili[campoDaModificare - 1];
                //Informazioni del campo modificato
                printer.accept("Inserisci le nuove informazioni del campo: ");
                campoModificato = "\"" + campiEvento.get(nomeCampo).get() + "\"";
            } else
                throw new FieldToModifyDoesNotExistsException();
        } catch (FieldToModifyDoesNotExistsException e) {
            printer.accept(e.getMessage());
            return;
        }
        String response = RequestComunication.getInstance().restRequest(
                getDomain() + "/modifica", "POST", getJsonModificaEvento(campoModificato, nomeCampo, idEvento)
        );

        if (Parser.getInstance().parseSuccess(response))
            printer.accept("L'evento è stato modificato!");
        else
            printer.accept("L'evento non è stato modificato!");
    }


    /**
     * Formatta il campo dell'evento da modificare in una stringa json che potrà essere inviata al server
     *
     * @param campoModificato valore del campo modificato
     * @param nomeCampo nome del campo da voler modificare
     * @param idEvento id dell'evento nel quale si vuole modificare un determinato campo
     * @return i campi passati , formattati in una stringa json
     */
    private String getJsonModificaEvento(String campoModificato, String nomeCampo, int idEvento) {
        HashMap<String, String> json = new HashMap<>();
        json.put("nomeCampo", nomeCampo);
        json.put("campoModificato", campoModificato);
        json.put("idEvento", Integer.toString(idEvento));

        Gson gson = new Gson();
        return gson.toJson(json);
    }



    public void annullaEvento(final int idEvento) {
        String response = BackendConnection.getInstance().checkAndRequest(
                getDomain() + "/annulla/" + LoggedUser.getInstance().getEmail() + ":" + idEvento, "GET",null
        );
        if (Parser.getInstance().parseSuccess(response))
            printer.accept("Evento annullato!");
    }
}