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

public class UserDisplayer {

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
                SoftwareManager.clearScreen();
                ReviewManager.visualizzaRecensioni(emailUtente);
                break;
            case "2":
                SoftwareManager.clearScreen();
                ReviewManager.scriviRecensione(emailUtente);
            default:
                SoftwareManager.clearScreen();
                printer.accept("Scelta errata! Riprovare!.\n");
                menuPartecipante(emailUtente);
        }
    }

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
                SoftwareManager.clearScreen();
                if(UserManager.getInstance().login())
                    menuModificaProfilo();
                break;
            case "2":
                SoftwareManager.clearScreen();
                ReviewManager.visualizzaRecensioni(LoggedUser.getInstance().getEmail());
                break;
            default:
                SoftwareManager.clearScreen();
                printer.accept("Scelta errata! Riprovare!.\n");
                menuUtente();
                break;
        }
    }

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
