package com.rv.justmeet.main.core;


import com.rv.justmeet.utility.RequestComunication;

import static com.rv.justmeet.utility.iOUtility.printer;

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


    public String getDomain() {
        return this.domain + ":" + this.port;
    }


    public String checkAndRequest(String path, String method) {
        if (Boolean.parseBoolean(
                RequestComunication.getInstance().restRequest(
                        getDomain() + "/testconnessione", "GET"
                )
        ))
            return RequestComunication.getInstance().restRequest(path, method);

        printer.accept("Connessione fallita!");
        System.exit(-1);
        return null;
    }

}
