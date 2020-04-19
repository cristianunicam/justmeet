package com.rv.justmeet.main.event;

import com.rv.justmeet.main.core.MySQLConnection;
import static com.rv.justmeet.main.core.SoftwareManager.printer;
import static com.rv.justmeet.main.core.SoftwareManager.scanner;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Lorenzo Romagnoli , Cristian Verdecchia
 *
 * Classe che fornisce metodi statici per la stampa della bacheca e dei singoli eventi
 */
public class EventsDisplayer {

    private EventsDisplayer(){}

    /**
     * Stampa la lista degli eventi presenti in bacheca.
     */
    public static void visualizzaBacheca(){
        try {
            ResultSet bacheca = MySQLConnection.getInstance().selectQueryReturnSet(
                    "SELECT eventsdb.id,categoriesdb.nome,titolo,descrizione,citta,data,prezzo FROM `eventsdb` JOIN `categoriesdb`ON categoria = categoriesdb.id");

            while (bacheca.next()) {
                printer.accept(bacheca.getString("id")+") \nCategoria: "+
                        bacheca.getString("categoriesdb.nome")+"\n"+
                        "titolo: "+
                                bacheca.getString("titolo")+"\n"+
                        "descrizione: "+
                                bacheca.getString("descrizione")+"\n"+
                        "citta: "+
                                bacheca.getString("citta")+"\n"+
                        "data: "+
                                bacheca.getString("data")+"\n"+
                        "prezzo: "+
                                bacheca.getString("prezzo")+"\n"
                );

            }
        }catch (SQLException e){
            printer.accept(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Stampa tutte le informazioni relative all'evento che viene scelto di visualizzare dall'utente.
     */
    public static void visualizzaEvento(){
        printer.accept("Inserisci l'ID dell'evento da voler visualizzare: ");

        try {
            int eventoDaMostrare = scanner.nextInt();
            ResultSet bacheca = MySQLConnection.getInstance().selectQueryReturnSet(
                    "SELECT eventsdb.id,categoriesdb.nome,titolo,descrizione,citta,via,data,oraInizio,oraFine,prezzo,maxPartecipanti,emailOrganizzatore " +
                            "FROM `eventsdb` JOIN `categoriesdb`ON categoria = categoriesdb.id WHERE eventsdb.id = "+eventoDaMostrare);
            bacheca.next();
            printer.accept(
                    "ID evento: "+
                            bacheca.getString("eventsdb.id")+"\n" +
                    "Categoria: "+
                    bacheca.getString("categoriesdb.nome")+"\n"+
                    "titolo: "+
                    bacheca.getString("titolo")+"\n"+
                    "descrizione: "+
                    bacheca.getString("descrizione")+"\n"+
                    "citta: "+
                    bacheca.getString("citta")+"\n"+
                    "via: "+
                    bacheca.getString("via")+"\n"+
                    "data: "+
                    bacheca.getString("data")+"\n"+
                    "ora inizio: "+
                    bacheca.getString("oraInizio")+"\n"+
                    "ora fine: "+
                    bacheca.getString("oraFine")+"\n"+
                    "prezzo: "+
                    bacheca.getString("prezzo")+"\n"+
                    "Numero massimo partecipanti: "+
                    bacheca.getString("maxPartecipanti")+"\n"+
                    "Email organizzatore: "+
                    bacheca.getString("emailOrganizzatore")
            );
        }catch (SQLException e){
            printer.accept(e.getMessage());
            System.exit(-1);
        }


    }
}
