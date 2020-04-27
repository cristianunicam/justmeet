package com.rv.justmeet.exceptions;

public class WrongTimeException extends Exception {
    public WrongTimeException() {
        super("L'ora da te inserita non rispetta gli standard, l'ora deve essere del tipo: HH:MM");
    }
}
