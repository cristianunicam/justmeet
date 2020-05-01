package com.rv.justmeet.main.core;


import com.rv.justmeet.utility.RequestComunication;

import static com.rv.justmeet.utility.IOUtility.printer;

/**
 * Classe per la connessione e gestione del database
 *
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public class BackendConnection {
    private static BackendConnection instance = null;
    private final String domain;
    private final String port;


    private BackendConnection() {
        this.domain = "http://localhost";
        this.port = "8080";
    }


    /**
     * Ritorna l'istanza di questa classe
     *
     * @return istanza classe BackendConnection
     */
    public static BackendConnection getInstance() {
        if (instance == null)
            instance = new BackendConnection();
        return instance;
    }

    /**
     * Ritorna il dominio con l'aggiunta della porta
     *
     * @return dominio
     */
    public String getDomain() {
        return this.domain + ":" + this.port;
    }


    /**
     * Controlla che il server sia raggiungibile ed esegue la richiesta
     * @param path Indirizzo al quale effettuare la richiesta
     * @param method il tipo di richiesta da effettuare
     * @return <code>String</code> la risposta della query effettuata
     */
    public String checkAndRequest(String path, String method , String parameters) {
        if (Boolean.parseBoolean(
                RequestComunication.getInstance().restRequest(
                        getDomain() + "/testconnessione", "GET" , parameters
                )
        ))
            return RequestComunication.getInstance().restRequest(path, method , parameters);

        printer.accept("Connessione fallita!");
        System.exit(-1);
        return null;
    }

}
