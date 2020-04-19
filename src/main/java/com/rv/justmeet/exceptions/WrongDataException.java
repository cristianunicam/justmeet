package com.rv.justmeet.exceptions;

public class WrongDataException extends Exception{
    public WrongDataException(){
        super("La data inserita non rispetta il formato richiesto.\n" +
                "Inserire una data nella forma: AAAA-MM-DD");
    }
}
