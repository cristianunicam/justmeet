package com.rv.justmeet.exceptions;

public class WrongCharactersNumber extends Exception {
    public WrongCharactersNumber(int min, int max) {
        super("Il testo inserito non rispetta gli standard.\n" +
                "Il numero di caratteri deve essere compreso tra: " + min + " e " + max + "\nRiprovare!");
    }
}
