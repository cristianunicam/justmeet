package com.rv.justmeet.exceptions;

public class FieldToModifyDoesNotExistsException extends Exception {
    public FieldToModifyDoesNotExistsException() {
        super("Il campo da voler modificare non esiste!");
    }
}
