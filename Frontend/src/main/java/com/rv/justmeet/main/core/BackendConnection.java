package com.rv.justmeet.main.core;


import com.rv.justmeet.utility.RequestComunication;

import static com.rv.justmeet.utility.IOUtility.printer;

/**
 * Classe per la verifica della connessione con il server
 *
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public class BackendConnection implements BackendConnectionInterface{
    private static BackendConnection instance = null;
    private final String domain;
    private final String port;


    private BackendConnection() {
        this.domain = "http://localhost";
        this.port = "8080";
    }


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
