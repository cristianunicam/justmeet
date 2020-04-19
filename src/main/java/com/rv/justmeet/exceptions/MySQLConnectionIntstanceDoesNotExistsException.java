package com.rv.justmeet.exceptions;

public class MySQLConnectionIntstanceDoesNotExistsException extends Exception{
    public MySQLConnectionIntstanceDoesNotExistsException(){
        super("Errore durante la connessione al database");
    }
}
