package com.rv.justmeet.main.user;

/**
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public interface LoggedUserInterface {

    /**
     * Scollega l'utente cancellando l'instanza di questa classe
     */
    void logout();


    /**
     * Ritorna l'email dell'utente
     *
     * @return string email dell'utente loggato
     */
    String getEmail();
}
