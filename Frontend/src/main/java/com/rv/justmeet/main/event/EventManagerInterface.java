package com.rv.justmeet.main.event;

/**
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 */
public interface EventManagerInterface {


    /**
     * Permette all'utente di creare un evento che verra' inserito in bacheca e salvato nel database
     */
    void aggiungiEvento();


    /**
     * Permette all'utente di modificare le informazioni di un evento da lui pubblicato
     *
     * @param idEvento id dell'evento che si vuole modificare
     */
    void modificaEvento(int idEvento);


    /**
     * Permette all'utente di annullare un evento da lui pubblicato
     */
    void annullaEvento(final int idEvento);
}
