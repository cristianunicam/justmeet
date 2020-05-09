package com.rv.justmeet.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

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
    public Map<String, Boolean> checkConnection(){
        return Collections.singletonMap("success",true);
    }
}
