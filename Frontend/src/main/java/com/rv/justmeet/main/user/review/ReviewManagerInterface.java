package com.rv.justmeet.main.user.review;

/**
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 */
public interface ReviewManagerInterface {

    /**
     * Metodo che permette all'utente di inserire una recensione.
     *
     * @param emailUtente email dell'utente che viene recensito
     */
    void scriviRecensione(String emailUtente);


    /**
     * Metodo che permette di visualizzare le recensioni presenti dell'utente
     *
     * @param emailUtente email dell'utente di cui vedere le recensioni
     */
    void visualizzaRecensioni(String emailUtente);

}
