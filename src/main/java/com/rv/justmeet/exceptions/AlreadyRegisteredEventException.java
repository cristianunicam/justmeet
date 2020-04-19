package com.rv.justmeet.exceptions;

public class AlreadyRegisteredEventException extends Exception {
    public AlreadyRegisteredEventException(){
        super("Partecipi già a questo evento!");
    }
}
