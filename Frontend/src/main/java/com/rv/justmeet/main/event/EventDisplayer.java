package com.rv.justmeet.main.event;

import com.rv.justmeet.main.core.BackendConnection;
import com.rv.justmeet.main.core.SoftwareManager;
import com.rv.justmeet.main.parser.EventParser;
import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.main.parser.UserParser;
import com.rv.justmeet.main.user.LoggedUser;
import com.rv.justmeet.main.user.UserDisplayer;
import com.rv.justmeet.main.user.UserManager;
import com.rv.justmeet.main.user.review.ReviewManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rv.justmeet.utility.IOUtility.*;

public class EventDisplayer {
    public static final String[] campiEventoBacheca = {
            "ID evento", "categoria", "Titolo", "Descrizione", "Citta'", "Data", "Prezzo"
    };

    public static final String[] campiEvento = {
            "Categoria", "Titolo", "Descrizione", "Citta'", "Via", "Data", "Ora inizio", "Ora fine", "Prezzo", "Numero massimo partecipanti", "Email organizzatore"
    };

    /**
     * Stampa la lista degli eventi presenti in bacheca.
     */
    public static void visualizzaBacheca() {
        String response = BackendConnection.getInstance().checkAndRequest(
                getDomain() + "geteventi", "GET",null
        );
        if (response.isEmpty()) {
            SoftwareManager.clearScreen();
            printer.accept("Non c'è nessun evento da visualizzare!");
            return;
        }
        stampaBacheca(
                EventParser.getInstance().parseBacheca(response)
        );
    }

    /**
     * Stampa tutte le informazioni relative all'evento che viene scelto di visualizzare dall'utente.
     */
    public static void visualizzaEvento() {
        printer.accept("Inserisci l'ID dell'evento da voler visualizzare: ");
        int eventoDaMostrare;
        if (((eventoDaMostrare = scanner.nextInt()) < 0)) {
            printer.accept("ERRORE, l'ID inserito non fa parte degli id degli eventi presenti!");
            return;
        }

        String response = BackendConnection.getInstance().checkAndRequest(
                getDomain() + "getevento/" + eventoDaMostrare, "GET",null
        );

        if (Parser.getInstance().parseJsonResponse(response, "false")) {
            printer.accept("L'evento selezionato non esiste!");
            return;
        }
        List<String> evento = EventParser.getInstance().parseEvento(response);
        stampaEvento(evento);
        //Controllo se l'evento visualizzato è stato pubblicato dall'utente
        String checkorganizzatore = BackendConnection.getInstance().checkAndRequest(
                getDomain() + "isorganizzatore/" + LoggedUser.getInstance().getEmail() + ":" + eventoDaMostrare, "GET",null
        );
        if (Parser.getInstance().parseSuccess(checkorganizzatore))
            menuEventoOrganizzatore(eventoDaMostrare);
        else
            menuEventoPartecipante(eventoDaMostrare);
    }

    /**
     * Metodo per visualizzare la lista degli eventi pubblicati da un utente
     */
    public static void visualizzaEventiPubblicati() {
        printer.accept("Lista eventi pubblicati: ");
        String response = BackendConnection.getInstance().checkAndRequest(
                getDomain() + "geteventipubblicati/" + LoggedUser.getInstance().getEmail(), "GET",null
        );

        if (response.isEmpty()) {
            SoftwareManager.clearScreen();
            printer.accept("Non hai pubblicato ancora alcun evento!");
            return;
        }

        List<String> eventiPubblicati = EventParser.getInstance().parseBacheca(response);
        stampaBacheca(eventiPubblicati);
    }

    /**
     * Metodo per visualizzare gli eventi ai quali partecipa l'utente
     */
    public static void visualizzaPartecipazioneEventi() {
        printer.accept("Lista eventi ai quali si partecipa: ");
        String response = BackendConnection.getInstance().checkAndRequest(
                getDomain() + "geteventipartecipante/" + LoggedUser.getInstance().getEmail(), "GET",null
        );
        if (response.isEmpty()) {
            SoftwareManager.clearScreen();
            printer.accept("Attualmente non partecipi a nessun evento!");
            return;
        }

        List<String> eventiPartecipi = EventParser.getInstance().parseBacheca(response);
        stampaBacheca(eventiPartecipi);
    }

    /**
     * Mostra il menù in caso in cui un utente non sia l'organizzatore
     *
     * @param idEvento ID dell'evento nel quale si sta navigando
     */
    private static void menuEventoPartecipante(final int idEvento) {
        printer.accept("\n\n0) Esci");
        String checkpartecipa = BackendConnection.getInstance().checkAndRequest(
                getDomain() + "ispartecipante/" + LoggedUser.getInstance().getEmail() + ":" + idEvento, "GET", null
        );
        boolean partecipa = Parser.getInstance().parseSuccess(checkpartecipa);
        if (partecipa)
            printer.accept("1) Annulla partecipazione");
        else
            printer.accept("1) Partecipa ad evento");
        printer.accept("2) Visualizza i partecipanti all'evento\n" +
                "Inserisci la tua scelta: ");

        switch (getString()) {
            case "0":
                return;
            case "1":
                SoftwareManager.clearScreen();
                if (partecipa)
                    UserManager.getInstance().annullaPartecipazione(idEvento);
                else
                    UserManager.getInstance().partecipaEvento(idEvento);
                break;
            case "2":
                SoftwareManager.clearScreen();
                visualizzaPartecipanti(idEvento);
                break;
            default:
                printer.accept("Il valore inserito è errato! Riprovare.");
                menuEventoPartecipante(idEvento);
        }
    }

    /**
     * Mostra il menù in caso in cui l'utente sia l'organizzatore dell'evento
     *
     * @param idEvento ID dell'evento nel quale si sta navigando
     */
    private static void menuEventoOrganizzatore(final int idEvento) {
        printer.accept("\n0) Esci\n" +
                "1) Modifica evento\n" +
                "2) Annulla evento\n" +
                "3) Visualizza partecipanti all'evento\n"+
                "Inserisci la tua scelta: "
        );

        switch (getString()) {
            case "0":
                return;
            case "1":
                SoftwareManager.clearScreen();
                EventManager.getInstance().modificaEvento(idEvento);
                break;
            case "2":
                SoftwareManager.clearScreen();
                EventManager.getInstance().annullaEvento(idEvento);
                break;
            case "3":
                SoftwareManager.clearScreen();
                visualizzaPartecipanti(idEvento);
                break;
            default:
                printer.accept("Valore inserito errato! Riprovare.");
                menuEventoOrganizzatore(idEvento);
                break;
        }
    }

    /**
     * Stampa la bacheca
     *
     * @param eventi eventi all'interno della bacheca
     */
    private static void stampaBacheca(List<String> eventi) {
        int y = 0;
        for (int z = 0; z < eventi.size() / campiEventoBacheca.length; z++) {
            for (String s : campiEventoBacheca) {
                printer.accept(s + ": " + eventi.get(y));
                y++;
            }
            printer.accept("");
        }
    }

    /**
     * Stampa i campi dell'evento
     *
     * @param evento lista contente tutti i campi di un evento
     */
    private static void stampaEvento(List<String> evento) {
        for (int x = 0; x < campiEvento.length; x++)
            printer.accept(campiEvento[x] + ": " + evento.get(x));
    }



    private static void visualizzaPartecipanti(int idEvento){
        String jsonString = BackendConnection.getInstance().checkAndRequest(
                "/eventi/visualizzapartecipanti/"+ idEvento, "GET",null
        );
        Map<Integer , String> utenti = UserParser.parsePartecipanti(jsonString);

        for(int x = 0 ; x< utenti.size() ; x++){
            printer.accept(x+1+")"+utenti.get(x));
        }
        printer.accept("\n0) Indietro\n" +
                "1) Visualizza profilo di un partecipante\n" +
                "Inserisci la tua scelta: "
        );

        switch (getString()) {
            case "0":
                return;
            case "1":
                UserDisplayer.scegliPartecipante(utenti);
                break;
            default:
                SoftwareManager.clearScreen();
                printer.accept("Scelta errata! Riprovare!.\n");
                visualizzaPartecipanti(idEvento);
        }
    }


    /**
     * Ritorna l'indirizzo che contiene le richieste che verranno effettuate da questa classe
     *
     * @return String contente il path
     */
    private static String getDomain() {
        return "/eventi/";
    }

}
