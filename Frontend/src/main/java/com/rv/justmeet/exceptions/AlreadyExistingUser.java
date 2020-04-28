package com.rv.justmeet.exceptions;

public class AlreadyExistingUser extends Exception {
    public AlreadyExistingUser() {
        super("Utente già esistente, provare inserendo una nuova mail!");
    }
}
