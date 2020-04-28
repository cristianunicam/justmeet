package com.rv.justmeet.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckConnection {

    @GetMapping(value="/testconnessione")
    public boolean checkConnection(){
        return true;
    }
}
