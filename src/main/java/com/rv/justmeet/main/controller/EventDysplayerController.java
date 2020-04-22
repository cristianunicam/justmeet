package com.rv.justmeet.main.controller;

import com.rv.justmeet.main.core.MySQLConnection;

import java.sql.ResultSet;

public class EventDysplayerController {

    public static boolean visualizzaEvento(final int eventoDaMostrare, final String email){
        return MySQLConnection.getInstance().selectQuery(
                "SELECT emailOrganizzatore " +
                        "FROM `eventsdb` " +
                        "WHERE id = "+ eventoDaMostrare +
                        " AND emailOrganizzatore = \""+ email +"\""
        );
    }

    public static ResultSet visualizzaBacheca(){
        return MySQLConnection.getInstance().selectQueryReturnSet(
                "SELECT eventsdb.id,categoriesdb.nome,titolo,descrizione,citta,data,prezzo FROM `eventsdb` JOIN `categoriesdb`ON categoria = categoriesdb.id"
        );
    }
}
