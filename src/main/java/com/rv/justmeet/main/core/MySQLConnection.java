package com.rv.justmeet.main.core;

import com.rv.justmeet.exceptions.MySQLConnectionIntstanceDoesNotExistsException;
import java.sql.*;

import static com.rv.justmeet.main.core.SoftwareManager.printer;

/**
 * Classe per la connessione e gestione del database
 *
 * @author Lorenzo Romagno, Cristian Verdecchia
 */
public class MySQLConnection {
    private static Connection _conn = null;
    private static Statement myStatement = null;
    private static MySQLConnection instance = null;

    MySQLConnection(String url, String user, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            _conn = DriverManager.getConnection(url, user, pass);
            myStatement = _conn.createStatement();
        }catch (ClassNotFoundException | SQLException e){
            printer.accept(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Dati i dati per la connessione al database crea l'instanza di MySQL
     *
     * @param url l'indirizzo del database al quale collegarsi
     * @param user utente al quale collegarsi nel database
     * @param pass password di user
     * @return MySQLConnection connessione al database
     */
    public static MySQLConnection getInstance(String url , String user , String pass){
        if(instance == null)
            instance = new MySQLConnection(url , user , pass);
        return instance;
    }

    /**
     * Ritorna l'instanza di MySQLConnection
     * @return MySQLConnection connessione al database
     */
    public static MySQLConnection getInstance(){
        try {
            if (instance == null)
                throw new MySQLConnectionIntstanceDoesNotExistsException();
        }catch (MySQLConnectionIntstanceDoesNotExistsException e){
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return instance;
    }

    /**
     * Scollega l'applicativo dal database
     */
    public static void scollegaDatabase(){
        try {
            myStatement.close();
            _conn.close();
        }catch (SQLException e){
            printer.accept(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Effettua query di tipo insert
     *
     * @param toInsert Query da effettuare
     * @return false se non ci sono risultati della query, true altrimenti
     */
    public boolean insertQuery(String toInsert) {
        try{
            return myStatement.execute(toInsert);
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return false;
    }

    /**
     * Effettua query di tipo select
     *
     * @param toSelect Query da effettuare
     * @return Boolean false se non sono stati trovati risultati durante la query
     *          true altrimenti
     */
    public Boolean selectQuery(String toSelect) {
        try {
            ResultSet result = myStatement.executeQuery(toSelect);
            if(!result.next())
                return false;
        }catch (SQLException e){
            System.out.println("ERRORE QUERY SELECT!");
            System.exit(-1);
        }
        return true;
    }

    /**
     * Effettua query di tipo select ritornando un set contenente i dati
     *
     * @param toSelect Query da effettuare
     * @return ResultSet dati corrispondenti al risultato della query
     */
    public ResultSet selectQueryReturnSet(String toSelect){
        ResultSet result = null;
        try {
            result = myStatement.executeQuery(toSelect);
        }catch (SQLException e){
            System.out.println("ERRORE QUERY SELECT!");
            System.exit(-1);
        }
        return result;
    }

}
