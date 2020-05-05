package com.rv.justmeet.main.user;

import com.rv.justmeet.exceptions.LoggedUserDoesNotExistsException;

import static com.rv.justmeet.utility.IOUtility.printer;

/**
 * Classe contente i dati dell'utente che ha effettuato con successo il login
 *
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public class LoggedUser implements LoggedUserInterface {
    private static LoggedUser instance = null;
    private final String email;

    private LoggedUser(String email) {
        this.email = email;
    }

    /**
     * Se non presente, crea un'instanza di LoggedUser, dato un indirizzo mail
     *
     * @param email email dell'utente che si è loggato
     * @return Instanza della classe
     */
    public static LoggedUser getInstance(String email) {
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
        } catch (LoggedUserDoesNotExistsException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return instance;
    }


    public void logout() {
        instance = null;
    }


    public String getEmail() {
        return email;
    }
}
