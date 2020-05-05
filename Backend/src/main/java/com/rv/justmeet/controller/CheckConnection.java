package com.rv.justmeet.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Classe utilizzata per la verifica della connessione
 *
 * @author Cristian Verdecchia Lorenzo Romagnoli
 */
@RestController
public class CheckConnection {
    /**
     * Metodo che ritorna true se viene effettuata la richiesta,
     * in questo modo il client può sapere che il server è raggiungibile
     *
     * @return true se la richiesta viene effettuata
     */
    @GetMapping(value="/testconnessione")
    public boolean checkConnection(){
        return true;
    }
}
