package com.rv.justmeet.main.user;

import com.google.gson.Gson;
import com.rv.justmeet.exceptions.*;
import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.utility.RequestComunication;

import java.util.HashMap;

import static com.rv.justmeet.utility.iOUtility.*;


/**
 * Classe singleton che gestisce un utente registrato o non
 *
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public class UserManager {
    private static final String[] campiUtente = {"email", "password", "nome", "cognome", "eta"};
    private static UserManager instance = null;

    private UserManager() {
    }

    /**
     * Metodo che ritorna l'instanza della classe
     *
     * @return Instanza della classe
     */
    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }


    /**
     * Metodo che permette all'utente di effettuare il login salvando i suoi
     * dati di accesso.
     */
    public boolean login() {
        //L'utente inserisce i dati di login
        LoginData datiLogin = inserimentoDatiLogin();
        //Utente corrispondente ai dati inseriti presente nel database
        String response = RequestComunication.getInstance().restSend(getDomain() + "login", "POST",
                "{ \"email\":\"" + datiLogin.email + "\", \"password\":\"" + datiLogin.password + "\"}");
        if (Parser.getInstance().parseSuccess(response)) {
            //Loggo l'utente
            LoggedUser.getInstance(datiLogin.getEmail());
        } else {
            return datiLoginErrati();
        }
        return true;
    }


    /**
     * Permettere all'utente di inserire i suoi dati per effettuare
     * la registrazione
     */
    public void registra() {
        HashMap<String, String> json = new HashMap<>();
        json.put(campiUtente[0], inserimentoEmailRegistrazione());
        json.put(campiUtente[1], inserisciStringa("password", 8, 30));
        json.put(campiUtente[2], inserisciStringa("nome", 3, 30));
        json.put(campiUtente[3], inserisciStringa("cognome", 3, 30));
        json.put(campiUtente[4], Integer.toString(inserisciInt("eta", 14, 105, "l'eta' deve essere maggiore di 14 anni")));

        printer.accept("I dati inseriti sono corretti? Effettuare la registrazione? (S/N)");
        String scelta;
        while (!(scelta = getString().toUpperCase()).equals("S")) {
            if ("N".equals(scelta)) {
                printer.accept("Registrazione non effettuata!");
                return;
            } else
                printer.accept("Risposta non accettata! Riprovare!");
        }
        Gson gson = new Gson();
        String response = RequestComunication.getInstance().restSend(getDomain() + "registrazione", "POST", gson.toJson(json));
        if (Parser.getInstance().parseSuccess(response))
            printer.accept("Registrazione effettuata!");
        else
            printer.accept("Registrazione non effettuata!");

    }


    /**
     * Metodo inserimento e controllo email durante la registrazione
     *
     * @return email utente
     */
    private String inserimentoEmailRegistrazione() {
        printer.accept("Per effettuare la registrazione, compila i seguenti campi: \nInserire l'indirizzo email: ");
        final String email = getString();
        try {
            if (!email.contains("@"))
                throw new WrongMailException();
            else {
                String response = RequestComunication.getInstance().restRequest(getDomain() + "controlloemail/" + email, "GET");
                if (Parser.getInstance().parseSuccess(response))
                    throw new AlreadyExistingUser();
            }
        } catch (WrongMailException | AlreadyExistingUser e) {
            printer.accept(e.getMessage());
            return inserimentoEmailRegistrazione();
        }
        return email;
    }


    /**
     * Metodo che permette all'utente di inserire i suoi dati e salvarli
     *
     * @return LoginData contenente email e password dell'utente
     */
    private LoginData inserimentoDatiLogin() {
        printer.accept("Inserire email: ");
        final String email = getString();
        printer.accept("Inserire password: ");
        final String password = getString();
        return new LoginData(email, password);
    }


    /**
     * In caso di dati di login errati, viene richiesto all'utente
     * di reinserirli, se l'utente accetta viene eseguito nuovamente il login
     **/
    private boolean datiLoginErrati() {
        String scelta;
        printer.accept("Dati d'accesso errati, riprovare? (S/N)");
        while (!(scelta = getString().toUpperCase()).equals("N")) {
            if ("S".equals(scelta)) {
                login();
                return true;
            } else {
                printer.accept("Risposta non accettata! Riprovare!");
            }
        }
        return false;
    }

    private String getDomain() {
        return "/utente/";
    }


    /**
     * Inner class contenente i dati di login di un utente
     */
    static final class LoginData {
        private final String email;
        private final String password;

        public LoginData(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

}
