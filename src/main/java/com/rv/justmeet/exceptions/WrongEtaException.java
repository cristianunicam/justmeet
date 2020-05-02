package com.rv.justmeet.exceptions;

public class WrongEtaException extends Exception{
    public WrongEtaException(String toPrint){
        super("Il dato inserito non rispetta gli standard.\n"+toPrint);
    }
}
