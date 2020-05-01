package com.rv.justmeet.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Repository contenete i dati di un utente e i metodi per l'esecuzione delle query nel database
 *
 * @author Lorenzo Romagnoli, Cristian verdecchia
 */
@Component
public class UserRepository {
    private String email;
    private String password;
    private String nome;
    private String cognome;
    private int eta;

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public int getEta() {
        return eta;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    /**
     * Metodo per effettuare la registrazione di un utente
     *
     * @param jdbcTemplate classe utilizzata per effettuare query al database
     * @return true se la registrazione è stata effettuata, false altrimenti
     */
    public boolean registrazione(JdbcTemplate jdbcTemplate){
        return jdbcTemplate.update("INSERT INTO userdb (email,password,nome,cognome,eta) VALUES (?,?,?,?,?)", this.email, this.password, this.nome, this.cognome, this.eta) == 1;
    }


    /**
     * Metodo per permettere ad utente di effettuare il login
     *
     * @param jdbcTemplate classe utilizzata per effettuare query al database
     * @return true se l'utente è presente, false altrimenti
     */
    public boolean login(JdbcTemplate jdbcTemplate){
        return !jdbcTemplate.queryForList("SELECT nome FROM `userdb` WHERE email = '" + this.email + "' AND password = '" + this.password + "'").isEmpty();
    }


    /**
     * Metodo per la partecipazione di un utente ad un evento
     *
     * @param jdbcTemplate classe utilizzata per effettuare query al database
     * @param emailUtente email dell'utente che vuole effettuare la registrazione ad un evento
     * @param idEvento ID dell'evento al quale l'utente si vuole registrare
     * @return true se la partecipazione è stata effettuata, false se l'utente è già registrato
     */
    public String partecipa(JdbcTemplate jdbcTemplate, String emailUtente, int idEvento) {
        //Controllo se il partecipante è già registrato a tale evento
        if(!jdbcTemplate.queryForList("SELECT idEvento FROM partecipantsdb WHERE idEvento = "+idEvento+" AND emailUtente = '"+emailUtente+"'").isEmpty())
            return "presente";

        //Prendo il numero massimo di partecipanti ad un determinato evento
        Long maxPartecipanti = (Long) jdbcTemplate.queryForList("SELECT maxPartecipanti FROM eventsdb WHERE id = "+idEvento).get(0).get("maxPartecipanti");

        //Prendo il numero di partecipanti registrati all'evento
        Long numPartecipanti = (Long) jdbcTemplate.queryForList("SELECT COUNT(idEvento) " +
                "FROM partecipantsdb "+
                "WHERE idEvento = "+idEvento).get(0).get("COUNT(idEvento)");

        //Controllo se l'evento è pieno
        if (numPartecipanti < maxPartecipanti) {
            jdbcTemplate.execute("INSERT INTO partecipantsdb (emailUtente,idEvento) SELECT userdb.email,eventsdb.id FROM userdb,eventsdb WHERE userdb.email = \"" + emailUtente + "\" AND eventsdb.id = " + idEvento);
            return "true";
        } else
            return "pieno";
    }


    /**
     * Metodo per controllare se l'email è già presente nel database
     *
     * @param jdbcTemplate classe utilizzata per effettuare query al database
     * @param emailUtente email che si vuole controllare se esiste
     * @return <code>true</code> se l'email già esiste, <code>false</code> altrimenti
     */
    public Boolean controlloEmail(JdbcTemplate jdbcTemplate, String emailUtente) {
        return !jdbcTemplate.queryForList("SELECT email" +
                " FROM userdb" +
                " WHERE email = '" + emailUtente + "'"
        ).isEmpty();
    }

    /**
     * Metodo per annullare la partecipazione di un utente ad un evento
     *
     * @param jdbcTemplate classe utilizzata per effettuare query al database
     * @param idEvento id dell'evento del quale si vuole annullare la partecipazione di un utente
     * @param emailUtente email dell'utente che vuole annullare la partecipazione
     * @return <code>true</code> se la cancellazione della partecipazione è avvenuta, <code>false</code> altrimenti
     */
    public Boolean annullaPartecipazione(JdbcTemplate jdbcTemplate, int idEvento,String emailUtente) {
        return jdbcTemplate.update(
                "DELETE FROM `partecipantsdb` " +
                        "WHERE idEvento = " + idEvento +
                        " AND emailUtente = '" + emailUtente + "'"
        ) == 1;
    }

    /**
     * Metodo utilizzato dopo l'esecuzione dei test da parte del frontend.
     * Permette di eliminare l'untente di test creato durante l'esecuzione dei vari test.
     *
     * @return true se l'utente è stato eliminato, false altrimenti
     */
    public boolean eliminaTest(JdbcTemplate jdbcTemplate){
        return jdbcTemplate.update("DELETE FROM userdb WHERE email = 'test@unicam.it'") == 1;
    }
}