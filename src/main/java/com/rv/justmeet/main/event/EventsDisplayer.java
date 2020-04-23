package com.rv.justmeet.main.event;

import com.rv.justmeet.main.controller.EventController;
import com.rv.justmeet.main.controller.EventDysplayerController;
import com.rv.justmeet.main.core.SoftwareManager;
import com.rv.justmeet.main.user.LoggedUser;

import static com.rv.justmeet.utility.iOUtility.printer;
import static com.rv.justmeet.utility.iOUtility.scanner;
import static com.rv.justmeet.utility.iOUtility.getString;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Lorenzo Romagnoli , Cristian Verdecchia
 *
 * Classe che fornisce metodi statici per la stampa della bacheca e dei singoli eventi
 */
public class EventsDisplayer {
    public static final String[] campiEventoStampabili = {
            "ID evento","categoria","Titolo","Descrizione","Citta'","Data","Prezzo"
    };

    private EventsDisplayer(){}

    /**
     * Stampa la lista degli eventi presenti in bacheca.
     */
    public static void visualizzaBacheca(){
        ResultSet bacheca = EventDysplayerController.visualizzaBacheca();
        if(!printResultSet(bacheca)) {
            SoftwareManager.clearScreen();
            printer.accept("Non c'è nessun evento da visualizzare!");
        }
    }

    /**
     * Stampa tutte le informazioni relative all'evento che viene scelto di visualizzare dall'utente.
     */
    public static void visualizzaEvento(){
        printer.accept("Inserisci l'ID dell'evento da voler visualizzare: ");
        int eventoDaMostrare = 0;
        try {
            if(((eventoDaMostrare = scanner.nextInt()) > EventController.getMaxIdEvento())||(eventoDaMostrare < 0)){
                printer.accept("ERRORE, l'ID inserito non fa parte degli id degli eventi presenti!");
                return;
            }
            ResultSet evento = EventController.getEvento(eventoDaMostrare);
            evento.next();

            //Scorro i campi dell'evento
            for(int x = 1; x < EventController.campiEvento.length; x++ )
                printer.accept(EventController.campiEvento[x-1]+": "+evento.getString(x));

        }catch (SQLException e){
            printer.accept(e.getMessage());
            System.exit(-1);
        }

        //Controllo se l'evento visualizzato è stato pubblicato dall'utente
        if(EventDysplayerController.visualizzaEvento(
                eventoDaMostrare,LoggedUser.getInstance().getEmail()
            ))
            menuEventoOrganizzatore(eventoDaMostrare);
        else
            menuEventoPartecipante(eventoDaMostrare);
    }


    /**
     * Metodo per visualizzare la lista degli eventi pubblicati da un utente
     */
    public static void visualizzaEventiPubblicati(){
        printer.accept("Lista eventi pubblicati: ");

        ResultSet eventiPubblicati = EventDysplayerController.visualizzaEventiPubblicati(LoggedUser.getInstance().getEmail());

        if(!printResultSet(eventiPubblicati)) {
            SoftwareManager.clearScreen();
            printer.accept("Non hai pubblicato ancora alcun evento!");
        }
    }


    /**
     * Metodo per visualizzare gli eventi ai quali partecipa l'utente
     */
    public static void visualizzaPartecipazioneEventi(){
        printer.accept("Lista eventi ai quali si partecipa: ");

        ResultSet eventiPartecipi = EventDysplayerController.visualizzaPartecipazioneEventi(LoggedUser.getInstance().getEmail());

        if(!printResultSet(eventiPartecipi)){
            SoftwareManager.clearScreen();
            printer.accept("Attualmente non partecipi a nessun evento!");
        }
    }


    /**
     * Mostra il menù in caso in cui un utente non sia l'organizzatore
     *
     * @param idEvento ID dell'evento nel quale si sta navigando
     */
    private static void menuEventoPartecipante(final int idEvento){
        boolean partecipa;
        printer.accept("\n\n0) Esci");
        if(partecipa = EventController.getPartecipazione(LoggedUser.getInstance().getEmail(),idEvento))
            printer.accept("1) Annulla partecipazione");
        else
            printer.accept("1) Partecipa ad evento");

        printer.accept("Inserisci la tua scelta: ");

        switch (getString()) {
            case "0":
                return;

            case "1":
                SoftwareManager.clearScreen();
                if(partecipa)
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
    private  static void menuEventoOrganizzatore(final int idEvento){
        printer.accept("\n0) Esci\n" +
                "1) Modifica evento\n" +
                "2) Annulla evento\n"+
                "Inserisci la tua scelta: "
        );
        switch (getString()){
            case "0":return;
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

    private static boolean printResultSet(ResultSet setEventi) {
        boolean risultato = false;
        try {
            while (setEventi.next()) {
                risultato = true;
                for (int x = 0; x < campiEventoStampabili.length; x++)
                    printer.accept(campiEventoStampabili[x] + ": " + setEventi.getString(x));
            }
        }catch (SQLException e){
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return risultato;
    }
}
