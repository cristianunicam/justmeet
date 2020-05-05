package com.rv.justmeet.utility;

/**
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 */
public interface RequestComunicationInterface {

    /**
     * Consente di effettuare un metodo post per inviare dati al server
     *
     * @param path       indirizzo al quale effettuare la richiesta
     * @param method     tipo di metodo http da eseguire
     * @param parameters parametri da inviare DA SCRIVERE SOTTO DOMRA DI STRINGA JSON
     */
    String restRequest(String path, String method, String parameters);
}
