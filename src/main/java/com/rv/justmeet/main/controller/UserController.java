package com.rv.justmeet.main.controller;

import com.rv.justmeet.main.core.MySQLConnection;

public abstract class UserController {

    public static boolean login(final String email, final String password){
        return MySQLConnection.getInstance().selectQuery(
                "SELECT * FROM `userdb` WHERE email = '"+email+"' AND password = '"+password+"'"
        );
    }
}
