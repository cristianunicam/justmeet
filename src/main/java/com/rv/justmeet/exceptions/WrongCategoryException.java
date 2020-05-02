package com.rv.justmeet.exceptions;

public class WrongCategoryException extends Exception{
    public WrongCategoryException(){
        super("La categoria inserita è errata, la categoria deve far parte delle seguenti: ");
    }
}
