package com.rv.justmeet.main.user;

/**
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public interface UserManagerInterface {

    /**
     * Permette all'utente di effettuare il login salvando i suoi
     * dati di accesso.
     */
    boolean login();

    /**
     * Permettere all'utente di inserire i suoi dati per effettuare
     * la registrazione
     */
    void registra();

    /**
     * Permette all'utente di partecipare ad un evento
     *
     * @param idEvento id dell'evento al quale si vuole partecipare
     */
    void partecipaEvento(final int idEvento);

    /**
     * Permette all'utente di annullare la partecipazione ad un evento
     *
     * @param idEvento id dell'evento al quale si vuole annullare la partecipazione
     */
    void annullaPartecipazione(final int idEvento);

    /**
     * Permette all'utente di inserire le nuove informazioni del campo che ha deciso di modificare
     *
     * @param sceltaCampo campo che l'utente ha scelto di modificare
     */
    void modificaProfilo(final int sceltaCampo);


}
