package com.rv.justmeet.main.core;

import com.rv.justmeet.main.event.EventDisplayer;
import com.rv.justmeet.main.event.EventManager;
import com.rv.justmeet.main.user.LoggedUser;
import com.rv.justmeet.main.user.UserDisplayer;
import com.rv.justmeet.main.user.UserManager;
import com.rv.justmeet.utility.IOUtility;
import static com.rv.justmeet.utility.IOUtility.clearScreen;


import static com.rv.justmeet.utility.IOUtility.getString;
import static com.rv.justmeet.utility.IOUtility.printer;

/**
 * Classe per la gestione dell'applicativo
 *
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public class SoftwareManager implements SoftwareManagerInterface{
    private static SoftwareManager instance = null;

    private SoftwareManager() {
    }

    public static SoftwareManager getInstance() {
        if (instance == null)
            instance = new SoftwareManager();
        return instance;
    }


    public void inizializzaSoftware() {
        clearScreen();
        BackendConnection.getInstance();
        this.inizializzaUtente();
    }

    /**
     * Metodo per inizializzare l'utente, eseguendo login o registrazione
     */
    private void inizializzaUtente() {
        while (true) {
            printer.accept(
                    "0) Per uscire dal programma\n" +
                            "1) Per effetturare il login \n" +
                            "2) Per effettuare la registrazione \n" +
                            "Inserisci la tua scelta: "
            );
            switch (getString()) {
                case "0":
                    clearScreen();
                    printer.accept("Grazie per aver utilizzato l'applicativo!");
                    System.exit(0);
                case "1":
                    if (!UserManager.getInstance().login())
                        inizializzaUtente();
                    else
                        menuIniziale();
                    clearScreen();
                    break;
                case "2":
                    clearScreen();
                    UserManager.getInstance().registra();
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
    private void menuIniziale() {
        printer.accept(
                "\n0) Logout\n" + "" +
                        "1) Per inserire un nuovo evento\n" +
                        "2) Per visualizzare la bacheca\n" +
                        "3) Per visualizzare un determinato evento o modificarlo\n" +
                        "4) Per visualizzare la lista degli eventi pubblicati\n" +
                        "5) Per visualizzare la lista degli eventi ai quali si partecipa\n" +
                        "6) Visualizza il tuo profilo\n"+
                        "7) Per visualizzare il profilo di un altro utente\n"+
                        "Inserisci la tua scelta: "
        );

        switch (getString()) {
            case "0":
                clearScreen();
                UserManager.getInstance().logout();
                inizializzaUtente();
                break;
            case "1":
                clearScreen();
                EventManager.getInstance().aggiungiEvento();
                break;
            case "2":
                clearScreen();
                EventDisplayer.visualizzaBacheca();
                break;
            case "3":
                clearScreen();
                EventDisplayer.visualizzaEvento();
                break;
            case "4":
                clearScreen();
                EventDisplayer.visualizzaEventiPubblicati();
                break;
            case "5":
                clearScreen();
                EventDisplayer.visualizzaPartecipazioneEventi();
                break;
            case "6":
                clearScreen();
                UserDisplayer.menuUtente();
                break;
            case "7":
                clearScreen();
                UserDisplayer.menuPartecipante(
                        IOUtility.inserisciStringa("email utente da cercare",5 , 50)
                );
                break;
        }
        menuIniziale();

    }
}
