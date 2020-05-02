package com.rv.justmeet.exceptions;

public class LoggedUserDoesNotExistsException extends Exception{
    public LoggedUserDoesNotExistsException(){
        super("ERRORE! UTENTE NON LOGGATO!");
    }
}
