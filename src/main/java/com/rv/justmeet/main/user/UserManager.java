package com.rv.justmeet.main.user;

import com.rv.justmeet.exceptions.*;
import com.rv.justmeet.main.controller.UserController;
import com.rv.justmeet.main.core.MySQLConnection;
import com.rv.justmeet.main.core.SoftwareManager;
import com.rv.justmeet.main.event.EventsManager;
import com.rv.justmeet.utility.iOUtility;

import java.io.IOException;
import static com.rv.justmeet.main.core.SoftwareManager.printer;
import static com.rv.justmeet.main.core.SoftwareManager.scanner;
import static com.rv.justmeet.main.core.SoftwareManager.stringReader;

/**
 * Classe singleton che gestisce un utente registrato o non
 *
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public class UserManager {
    private static UserManager instance = null;

    private UserManager(){}

    /**
     * Metodo che ritorna l'instanza della classe
     *
     * @return Instanza della classe
     */
    public static UserManager getInstance(){
        if(instance == null)
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
        if(UserController.login(datiLogin.getEmail(),datiLogin.getPassword())){
            //Loggo l'utente
            LoggedUser.getInstance(datiLogin.getEmail());
        }else{
            return datiLoginErrati();
        }
        return true;
    }


    /**
     * Esegue la registrazione di un nuovo utente salvandolo nel database
     */
    public void registra() {
        MySQLConnection.getInstance().insertQuery(creaQueryRegistrazione());
        printer.accept("Registrazione effettuata!");
    }


    /**
     * Permettere all'utente di inserire i suoi dati per effettuare
     * la registrazione
     *
     * @return String contenente la query da effettuare
     */
    private String creaQueryRegistrazione() {
            final String email = inserimentoEmailRegistrazione();
            final String password = iOUtility.inserisciStringa("password", 8,30);
            final String nome = iOUtility.inserisciStringa("nome",3,30);
            final String cognome = iOUtility.inserisciStringa("cognome",30,30);
            final int eta = iOUtility.inserisciInt("eta",14,105,"l'eta' deve essere maggiore di 14 anni");
            printer.accept("I dati inseriti sono corretti? Effettuare la registrazione? (S/N)");
            String scelta;
            while(!(scelta = SoftwareManager.getString().toUpperCase()).equals("S")) {
                if ("N".equals(scelta)) {
                    printer.accept("Registrazione non effettuata!");
                    return null;
                } else
                    printer.accept("Risposta non accettata! Riprovare!");
            }
            return "INSERT INTO `userdb` (email,password,nome,cognome,eta) "+
                    "VALUES (\""+email+"\",\""+password+"\",\""+nome+"\",\""+cognome+"\","+eta+")";
    }


    /**
     * Metodo inserimento e controllo email durante la registrazione
     *
     * @return email utente
     */
    private String inserimentoEmailRegistrazione() {
        printer.accept("Per effettuare la registrazione, compila i seguenti campi: \nInserire l'indirizzo email: ");
        final String email = SoftwareManager.getString();
        try {
            if (!email.contains("@"))
                throw new WrongMailException();
            else if(MySQLConnection.getInstance().selectQuery(
                    "SELECT * FROM `userdb` WHERE email = '"+email+"'"
            ))
                throw new AlreadyExistingUser();
        }catch (WrongMailException | AlreadyExistingUser e){
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
    private LoginData inserimentoDatiLogin(){
        printer.accept("Inserire email: ");
        final String email = SoftwareManager.getString();
        printer.accept("Inserire password: ");
        final String password = SoftwareManager.getString();
        return new LoginData(email, password);
    }


    /**
     * In caso di dati di login errati, viene richiesto all'utente
     * di reinserirli, se l'utente accetta viene eseguito nuovamente il login
     **/
    private boolean datiLoginErrati(){
        String scelta;
        printer.accept("Dati d'accesso errati, riprovare? (S/N)");
        try {
            while(!(scelta = stringReader.readLine().toUpperCase()).equals("N")) {
                if ("S".equals(scelta)) {
                    login();
                    return true;
                } else {
                    printer.accept("Risposta non accettata! Riprovare!");
                }
            }
        }catch (IOException e){
            printer.accept("Errore nell'inserimento!");
            datiLoginErrati();
        }
        return false;
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
