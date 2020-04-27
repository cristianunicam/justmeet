package com.rv.justmeet.exceptions;

public class WrongMailException extends Exception {
    public WrongMailException() {
        super("Inserisci una mail valida! Riprovare!");
    }
}
