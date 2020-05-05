package com.rv.justmeet.main.user;

import com.rv.justmeet.main.core.BackendConnection;
import com.rv.justmeet.main.core.SoftwareManager;
import com.rv.justmeet.main.parser.UserParser;
import com.rv.justmeet.main.user.review.ReviewManager;
import com.rv.justmeet.utility.IOUtility;

import java.util.List;
import java.util.Map;

import static com.rv.justmeet.utility.IOUtility.getString;
import static com.rv.justmeet.utility.IOUtility.printer;
import static com.rv.justmeet.utility.IOUtility.clearScreen;

/**
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 *
 * Classe di utility che fornisce metodi per la stampa dei profili degli utenti e per i relativi menu
 */
public class UserDisplayer {

    /**
     * Permette all'utente di scegliere un utente di cui poi visualizzarne il profilo
     *
     * @param utenti Map contenente le email degli utenti da poter visualizzare
     */
    public static void scegliPartecipante(Map<Integer , String> utenti){
        printer.accept("Inserisci il numero dell'utente da visualizzare.");
        int scelta = IOUtility.scanner.nextInt()-1;

        if(scelta >= utenti.size()){
            printer.accept("Scelta non valida! Riprovare!");
            scegliPartecipante(utenti);
            return;
        }
        menuPartecipante(utenti.get(scelta));
    }

    /**
     * Mostra il menu delle possibili azioni che possono essere eseguite guardando il profilo di un'altro utente
     *
     * @param emailUtente email dell'utente di cui si sta guardando il profilo
     */
    public static void menuPartecipante(String emailUtente){
        if(visualizzaProfilo(emailUtente)){
            printer.accept("Nessun utente trovato!");
            return;
        }
        printer.accept("\n0) Esci\n" +
                "1) Visualizza recensioni\n" +
                "2) Scrivi recensione\n"+
                "Inserisci la tua scelta: "
        );

        switch (getString()) {
            case "0":
                return;
            case "1":
                clearScreen();
                ReviewManager.getInstance().visualizzaRecensioni(emailUtente);
                break;
            case "2":
                clearScreen();
                ReviewManager.getInstance().scriviRecensione(emailUtente);
            default:
                clearScreen();
                printer.accept("Scelta errata! Riprovare!.\n");
                menuPartecipante(emailUtente);
        }
    }

    /**
     * Mostra il menu delle possibili azioni che si possono eseguire guardando il proprio profilo
     */
    public static void menuUtente(){
        if(visualizzaProfilo(LoggedUser.getInstance().getEmail())) {
            printer.accept("Nessun utente trovato!");
            return;
        }
        printer.accept("\n0) Indietro\n" +
                "1) Modifica profilo\n" +
                "2) Visualizza le tue recensioni\n"+
                "Inserisci la tua scelta: "
        );

        switch (getString()) {
            case "0":
                return;
            case "1":
                clearScreen();
                if(UserManager.getInstance().login())
                    menuModificaProfilo();
                break;
            case "2":
                clearScreen();
                ReviewManager.getInstance().visualizzaRecensioni(LoggedUser.getInstance().getEmail());
                break;
            default:
                clearScreen();
                printer.accept("Scelta errata! Riprovare!.\n");
                menuUtente();
                break;
        }
    }

    /**
     * Mostra e permette di scegliere l'informazione del proprio profilo da modificare
     */
    public static void menuModificaProfilo(){
        printer.accept("\n0) Indietro\n" +
                "1) Modifica nome\n"+
                "2) Modifica cognome\n"+
                "3) Modifica eta"+
                "4) Modifica password\n"
        );
        UserManager.getInstance().modificaProfilo(IOUtility.scanner.nextInt());
        System.exit(0);
    }

    /**
     * Mostra le informazioni relative al profilo di un utente
     *
     * @param emailUtente email dell'utetne di cui si vuole visualizzare le informazioni
     * @return <code>true</code> se e' stato trovato l'utente cercato ,
     *         <code>false</code> se non e' stato trovato nessun utente data l'email inserita
     */
    private static boolean visualizzaProfilo(String emailUtente){
        String jsonString = BackendConnection.getInstance().checkAndRequest(
                "/utente/getprofilo/"+ emailUtente, "GET",null
        );
        List<String> datiUtente = UserParser.getInstance().parseDatiUtente(jsonString);
        if(datiUtente.size() == 0)
            return true;
        for (String s : datiUtente)
            printer.accept(s);
        return false;
    }


}
