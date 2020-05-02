package com.rv.justmeet.exceptions;

public class MaxPartecipantsException extends Exception {
    public MaxPartecipantsException(){
        super("Il numero massimo di partecipanti per il dato evento è stato raggiunto");
    }
}
