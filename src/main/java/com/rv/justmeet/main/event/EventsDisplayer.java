package com.rv.justmeet.main.event;

import com.rv.justmeet.main.core.SoftwareManager;
import com.rv.justmeet.main.parser.EventParser;
import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.main.user.LoggedUser;
import com.rv.justmeet.utility.RequestComunication;

import java.util.List;

import static com.rv.justmeet.utility.iOUtility.*;

public class EventsDisplayer {
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
        String response = RequestComunication.getInstance().restRequest(getDomain() + "geteventi", "GET");
        if (response.isEmpty()) {
            SoftwareManager.clearScreen();
            printer.accept("Non c'è nessun evento da visualizzare!");
            return;
        }
        printBacheca(
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

        String response = RequestComunication.getInstance().restRequest(
                getDomain() + "getevento/" + eventoDaMostrare, "GET"
        );

        if (Parser.getInstance().parseJsonResponse(response, "false")) {
            printer.accept("L'evento selezionato non esiste!");
            return;
        }
        List<String> evento = EventParser.getInstance().parseEvento(response);
        printEvento(evento);
        //Controllo se l'evento visualizzato è stato pubblicato dall'utente
        String checkorganizzatore = RequestComunication.getInstance().restRequest(
                getDomain() + "isorganizzatore/" + LoggedUser.getInstance().getEmail() + ":" + eventoDaMostrare, "GET");
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
        String response = RequestComunication.getInstance().restRequest(
                getDomain() + "geteventipubblicati/" + LoggedUser.getInstance().getEmail(), "GET");

        if (response.isEmpty()) {
            SoftwareManager.clearScreen();
            printer.accept("Non hai pubblicato ancora alcun evento!");
            return;
        }

        List<String> eventiPubblicati = EventParser.getInstance().parseBacheca(response);
        printBacheca(eventiPubblicati);
    }

    /**
     * Metodo per visualizzare gli eventi ai quali partecipa l'utente
     */
    public static void visualizzaPartecipazioneEventi() {
        printer.accept("Lista eventi ai quali si partecipa: ");
        String response = RequestComunication.getInstance().restRequest(
                getDomain() + "geteventipartecipante/" + LoggedUser.getInstance().getEmail(), "GET"
        );
        if (response.isEmpty()) {
            SoftwareManager.clearScreen();
            printer.accept("Attualmente non partecipi a nessun evento!");
            return;
        }

        List<String> eventiPartecipi = EventParser.getInstance().parseBacheca(response);
        printBacheca(eventiPartecipi);
    }

    /**
     * Mostra il menù in caso in cui un utente non sia l'organizzatore
     *
     * @param idEvento ID dell'evento nel quale si sta navigando
     */
    private static void menuEventoPartecipante(final int idEvento) {
        printer.accept("\n\n0) Esci");
        String checkpartecipa = RequestComunication.getInstance().restRequest(
                getDomain() + "ispartecipante/" + LoggedUser.getInstance().getEmail() + ":" + idEvento, "GET"
        );
        boolean partecipa = Parser.getInstance().parseSuccess(checkpartecipa);
        if (partecipa)
            printer.accept("1) Annulla partecipazione");
        else
            printer.accept("1) Partecipa ad evento");

        printer.accept("Inserisci la tua scelta: ");

        switch (getString()) {
            case "0":
                return;

            case "1":
                SoftwareManager.clearScreen();
                if (partecipa)
                    EventsManager.getInstance().annullaPartecipazione(idEvento);
                else
                    EventsManager.getInstance().partecipaEvento(idEvento);
                break;
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
                "Inserisci la tua scelta: "
        );

        switch (getString()) {
            case "0":
                return;
            case "1":
                SoftwareManager.clearScreen();
                EventsManager.getInstance().modificaEvento(idEvento);
                break;
            case "2":
                SoftwareManager.clearScreen();
                EventsManager.getInstance().annullaEvento(idEvento);
                break;
        }
    }


    private static void printBacheca(List<String> eventi) {
        int y = 0;
        for (int z = 0; z < eventi.size() / campiEventoBacheca.length; z++) {
            for (String s : campiEventoBacheca) {
                printer.accept(s + ": " + eventi.get(y));
                y++;
            }
            printer.accept("");
        }
    }


    private static void printEvento(List<String> evento) {
        for (int x = 0; x < campiEvento.length; x++)
            printer.accept(campiEvento[x] + ": " + evento.get(x));
    }

    private static String getDomain() {
        return "/eventi/";
    }

}
