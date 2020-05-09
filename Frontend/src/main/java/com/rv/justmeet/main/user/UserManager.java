package com.rv.justmeet.main.user;

import com.google.gson.Gson;
import com.rv.justmeet.exceptions.AlreadyExistingUser;
import com.rv.justmeet.exceptions.WrongMailException;
import com.rv.justmeet.main.core.BackendConnection;
import com.rv.justmeet.main.parser.Parser;
import com.rv.justmeet.utility.IOUtility;
import com.rv.justmeet.utility.RequestComunication;

import java.util.HashMap;

import static com.rv.justmeet.utility.IOUtility.*;


/**
 * Classe che gestisce un utente registrato o non
 *
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public class UserManager implements UserManagerInterface {
    private static final String[] campiUtente = {"email", "password", "nome", "cognome", "eta"};
    private static UserManager instance = null;

    private UserManager() {
    }


    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }


    public boolean login() {
        //L'utente inserisce i dati di login
        LoginData datiLogin = inserimentoDatiLogin();
        //Utente corrispondente ai dati inseriti presente nel database
        String response = BackendConnection.getInstance().checkAndRequest(getDomain() + "login", "POST",
                "{ \"email\":\"" + datiLogin.email + "\", \"password\":\"" + datiLogin.password + "\"}");
        if (Parser.getInstance().parseSuccess(response))
            LoggedUser.getInstance(datiLogin.getEmail());
        else
            return datiLoginErrati();
        clearScreen();
        return true;
    }

    public void logout(){
        LoggedUser.getInstance().logout();
        printer.accept("L'utente è stato disconnesso!");
    }

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
        String response = BackendConnection.getInstance().checkAndRequest(getDomain() + "registrazione", "POST", gson.toJson(json));
        clearScreen();
        if (Parser.getInstance().parseSuccess(response))
            printer.accept("Registrazione effettuata!");
        else
            printer.accept("Registrazione non effettuata!");

    }



    public void partecipaEvento(final int idEvento) {
        String response = BackendConnection.getInstance().checkAndRequest(
                "/utente/partecipa/" + LoggedUser.getInstance().getEmail() + ":" + idEvento, "GET",null
        );
        String risposta;
        if ((risposta = Parser.getInstance().parseJsonResponseString(response)).equals("true"))
            printer.accept("Partecipazione effettuata!");
        else if (risposta.equals("presente"))
            printer.accept("Errore, l'utente partecipa già a questo evento!");
        else if (risposta.equals("pieno"))
            printer.accept("Errore, l'evento ha raggiunto il numero massimo di partecipanti!");
    }



    public void annullaPartecipazione(final int idEvento) {
        String response = BackendConnection.getInstance().checkAndRequest(
                "/utente/annullapartecipazione/" + LoggedUser.getInstance().getEmail() + ":" + idEvento, "GET",null
        );
        if (Parser.getInstance().parseSuccess(response))//vedere quale parse fare in base alla risposta
            printer.accept("Eliminazione effettutata");
        else
            printer.accept("Eliminazione non effettuata");
    }


    /**
     * Metodo per l'inserimento e controllo dell'email durante la registrazione
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
                String response = BackendConnection.getInstance().checkAndRequest(
                        getDomain() + "controlloemail/" + email, "GET",null
                );

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
                return login();
            } else
                printer.accept("Risposta non accettata! Riprovare!");
        }
        return false;
    }


    public void modificaProfilo(final int sceltaCampo){
        String nomeCampo = campiUtente[sceltaCampo];
        String campoModificato = null;
        int eta = 0;
        boolean uguali = false;

        do {
            switch (sceltaCampo) {
                case 1:
                    uguali = (campoModificato = IOUtility.inserisciStringa("password", 3, 50)).equals(
                            IOUtility.inserisciStringa("password",3,50));
                    break;
                case 2:
                    uguali = (campoModificato = IOUtility.inserisciStringa("nome", 3, 30)).equals(
                            IOUtility.inserisciStringa("nome",3,30));
                    break;
                case 3:
                    uguali = (IOUtility.inserisciStringa("cognome", 3, 30)).equals(
                            IOUtility.inserisciStringa("cognome",3,30));
                    break;
                case 4:
                    uguali = (eta = IOUtility.inserisciInt("eta'", 14, 100, "l'eta' deve essere maggiore di 14 anni")) ==
                    IOUtility.inserisciInt("eta' di nuovo", 14, 100, "l'eta' deve essere maggiore di 14 anni");
                    break;
            }
            if(!uguali)
                printer.accept("I campi inseriti non coincidono! Riprovare!");
        }while(!uguali);


        if(Parser.getInstance().parseSuccess(
                BackendConnection.getInstance().checkAndRequest(
                getDomain()+"modifica/"+LoggedUser.getInstance().getEmail()+":"+nomeCampo+":"+(campoModificato == null ? eta : campoModificato),"GET",null
                )
            )
        )
            printer.accept("La modifica e' stata effettuata");
        else
            printer.accept("Errore nell'esecuzione della query, modifica non effettuata");
    }

    /**
     * Metodo che fornisce il path per le richieste rest relative agli utenti
     *
     * @return il path relativo agli utenti
     */
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
