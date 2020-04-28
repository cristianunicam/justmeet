package com.rv.justmeet.exceptions;

public class EventApplyException extends Exception {
    public EventApplyException() {
        super("L'evento da te scelto non esiste. Riprovare!");
    }
}
