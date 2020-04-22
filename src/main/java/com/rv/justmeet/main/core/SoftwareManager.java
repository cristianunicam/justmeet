package com.rv.justmeet.main.core;

import com.rv.justmeet.main.event.EventsDisplayer;
import com.rv.justmeet.main.event.EventsManager;
import com.rv.justmeet.main.user.LoggedUser;
import com.rv.justmeet.main.user.UserManager;

import static com.rv.justmeet.utility.iOUtility.getString;
import static com.rv.justmeet.utility.iOUtility.printer;

import java.io.IOException;

/**
 * Classe per la gestione dell'applicativo
 *
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public class SoftwareManager {
    private static SoftwareManager instance = null;

    private SoftwareManager(){}

    public static SoftwareManager getInstance(){
        if(instance == null)
            instance = new SoftwareManager();
        return instance;
    }


    /**
     * Metodo che inizializza la connessione con il database
     * ed esegue l'inizializzazione dell'utente
     */
    public void inizializzaSoftware() {
        MySQLConnection.getInstance(
                "jdbc:mysql://localhost/justmeet?useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "1234"
        );
        this.inizializzaUtente();
    }

    /**
     * Metodo per inizializzare l'utente, eseguendo login o registrazione
     */
    private void inizializzaUtente() {
        while(true) {
            clearScreen();
            printer.accept(
                    "0) Per uscire dal programma\n"+
                            "1) Per effetturare il login \n" +
                            "2) Per effettuare la registrazione \n" +
                            "Inserisci la tua scelta: "
            );
            switch (getString()) {
                case "0":
                    MySQLConnection.scollegaDatabase();
                    clearScreen();
                    printer.accept("Grazie per aver utilizzato l'applicativo!");
                    System.exit(0);
                case "1":
                    if(!UserManager.getInstance().login())
                        inizializzaUtente();
                    else
                        gestioneBacheca();
                    clearScreen();
                    break;
                case "2":
                    UserManager.getInstance().registra();
                    clearScreen();
                    break;
                default:
                    printer.accept("Scelta errata, riprovare: ");
                    inizializzaUtente();
                    break;
            }
        }
    }


    /**
     * Metodo che stampa il menù ed esegue i vari metodi al suo interno in base
     * alla scelta dell'utente
     */
    private void gestioneBacheca(){
        printer.accept(
                "\n0) Logout\n"+"" +
                        "1) Per inserire un nuovo evento\n"+
                        "2) Per visualizzare la bacheca\n"+
                        "3) Per visualizzare un determinato evento o modificarlo\n"+
                        "4) Per visualizzare la lista degli eventi pubblicati\n"+
                        "5) Per visualizzare la lista degli eventi ai quali si partecipa\n"+
                        "Inserisci la tua scelta: "
        );
        switch (getString()) {
            case "0":
                LoggedUser.logout();
                clearScreen();
                inizializzaUtente();
                break;
            case "1":
                clearScreen();
                EventsManager.getInstance().aggiungiEvento();
                break;
            case "2":
                clearScreen();
                EventsDisplayer.visualizzaBacheca();
                break;
            case "3":
                clearScreen();
                EventsDisplayer.visualizzaEvento();
                break;
            case "4":
                clearScreen();
                EventsDisplayer.visualizzaEventiPubblicati();
                break;
            case "5":
                clearScreen();
                EventsDisplayer.visualizzaPartecipazioneEventi();
                break;
        }
        gestioneBacheca();

    }

    /**
     * "Pulisce" lo schermo in base al tipo di sistema operativo in utilizzo
     */
    public static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else {
                printer.accept("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            printer.accept(e.getMessage());
        } catch (SecurityException e) {
            printer.accept("\033[H\033[2J");
            System.out.flush();
        }
    }
}
