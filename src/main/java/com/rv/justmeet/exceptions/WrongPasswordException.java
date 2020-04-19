package com.rv.justmeet.exceptions;

public class WrongPasswordException extends Exception{
    public WrongPasswordException(){
        super("La password inserita non rispetta gli standard.\nLa lunghezza deve essere compresa tra 8 e 30 caratteri\n");
    }
}
