package com.rv.justmeet.main.core;

public interface BackendConnectionInterface {

    /**
     * Controlla che il server sia raggiungibile ed esegue la richiesta
     *
     * @param path Indirizzo al quale effettuare la richiesta
     * @param method il tipo di richiesta da effettuare
     * @return <code>String</code> la risposta della query effettuata
     */
    String checkAndRequest(String path, String method, String parameters);
}
