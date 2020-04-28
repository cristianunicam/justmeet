package com.rv.justmeet.main.event;

import com.google.gson.Gson;
import com.rv.justmeet.exceptions.FieldToModifyDoesNotExistsException;
import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.main.user.LoggedUser;
import com.rv.justmeet.utility.RequestComunication;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.rv.justmeet.utility.iOUtility.*;


/**
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 * <p>
 * Classe Singleton che gestisce le azioni relaive agli eventi
 */
public class EventsManager {
    private static EventsManager instance = null;
    private final Map<String, Supplier<?>> campiEvento = new HashMap<>();
    private final String[] campiDatabaseModificabili = {
            "titolo", "descrizione", "citta", "via", "data", "oraInizio", "oraFine", "prezzo", "maxPartecipanti"
    };
    private final String[] campiEventoModificabili = {
            "Titolo", "Descrizione", "Citta'", "Via", "Data", "Ora inizio", "Ora fine", "Prezzo", "Numero massimo partecipanti"
    };

    private EventsManager() {
    }

    public static EventsManager getInstance() {
        if (instance == null) {
            instance = new EventsManager();
            instance.mapInit();
        }
        return instance;
    }

    private static String getDomain() {
        return "/eventi/";
    }

    /**
     * Inizializzazione HashMap che conterrà i metodi per l'inserimento dei dati di un evento
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
        campiEvento.put("maxPartecipanti", () -> inserisciInt(
                "numero massimo partecipanti", 2, 100000, "Il numero di partecipanti deve essere compreso tra 2 e 100000"));
    }

    /**
     * Permette di creare un evento che verra' inserito in bacheca e salvato nel database
     */
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
        json.put("maxPartecipanti", campiEvento.get("maxPartecipanti").get().toString());
        json.put("emailOrganizzatore", LoggedUser.getInstance().getEmail());

        Gson gson = new Gson();
        String response = RequestComunication.getInstance().restSend(getDomain() + "/inserimento", "POST", gson.toJson(json));

        if (Parser.getInstance().parseSuccess(response))
            printer.accept("Evento inserito!");
        else
            printer.accept("Evento non inserito!");

    }

    /**
     * Permette di scegliere un evento a cui partecipare. Salvando poi la partecipazione nel database
     */
    public void partecipaEvento(final int idEvento) {
        String response = RequestComunication.getInstance().restRequest(
                "/utente/partecipa/" + LoggedUser.getInstance().getEmail() + ":" + idEvento, "GET");
        String risposta;
        if ((risposta = Parser.getInstance().parseJsonresponseString(response)).equals("true"))
            printer.accept("Partecipazione effettuata!");
        else if (risposta.equals("presente"))
            printer.accept("Errore, l'utente partecipa già a questo evento!");
        else if (risposta.equals("pieno"))
            printer.accept("Errore, l'evento ha raggiunto il numero massimo di partecipanti!");
    }

    /**
     * Metodo per annullare la partecipazione ad un evento
     *
     * @param idEvento id dell'evento del quale si vuole annullare la partecipazione
     */
    public void annullaPartecipazione(final int idEvento) {
        String response = RequestComunication.getInstance().restRequest(
                "/utente/annullapartecipazione/" + LoggedUser.getInstance().getEmail() + ":" + idEvento, "GET");
        if (Parser.getInstance().parseSuccess(response))//vedere quale parse fare in base alla risposta
            printer.accept("Eliminazione effettutata");
        else
            printer.accept("Eliminazione non effettuata");
    }

    /**
     * Metodo per la modifica di un evento
     *
     * @param idEvento id dell'evento che si vuole modificare
     */
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
        String response = RequestComunication.getInstance().restSend(
                getDomain() + "modifica", "POST", getJsonModificaEvento(campoModificato, nomeCampo, idEvento)
        );

        if (Parser.getInstance().parseSuccess(response))
            printer.accept("L'evento è stato modificato!");
        else
            printer.accept("L'evento non è stato modificato!");
    }

    private String getJsonModificaEvento(String campoModificato, String nomeCampo, int idEvento) {
        HashMap<String, String> json = new HashMap<>();
        json.put("nomeCampo", nomeCampo);
        json.put("campoModificato", campoModificato);
        json.put("idEvento", Integer.toString(idEvento));

        Gson gson = new Gson();
        return gson.toJson(json);
    }

    /**
     * Metodo per annullare un evento
     */
    public void annullaEvento(final int idEvento) {
        String response = RequestComunication.getInstance().restRequest(
                getDomain() + "annulla/" + LoggedUser.getInstance().getEmail() + ":" + idEvento, "GET");
        if (Parser.getInstance().parseSuccess(response))
            printer.accept("Evento annullato!");
    }
}