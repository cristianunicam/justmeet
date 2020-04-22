package com.rv.justmeet.main.user;

import com.rv.justmeet.exceptions.LoggedUserDoesNotExistsException;

import static com.rv.justmeet.utility.iOUtility.printer;

/**
 * Classe contente i dati a seguito di una connessione dell'utente al database
 *
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public class LoggedUser {
    private static LoggedUser instance = null;
    private final String email;

    private LoggedUser(String email){
        this.email = email;
    }

    /**
     * Se non presente, crea un'instanza di LoggedUser, dato un indirizzo mail
     *
     * @param email email dell'utente che si è loggato
     * @return Instanza della classe
     */
    public static LoggedUser getInstance(String email){
        if (instance == null)
            instance = new LoggedUser(email);
        return instance;
    }

    /**
     * Se presente ritorna un'instanza di LoggedUser.
     *
     * @return instanza della classe
     */
    public static LoggedUser getInstance() {
        try {
            if (instance == null)
                throw new LoggedUserDoesNotExistsException();
        }catch (LoggedUserDoesNotExistsException e){
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return instance;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Scollega l'utente cancellando l'instanza di questa classe
     */
    public static void logout(){
        instance = null;
    }

}
