package com.rv.justmeet.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Repository contenete i metodi per la gestione delle recensioni
 *
 * @author Lorenzo Romagnoli, Cristian verdecchia
 */
@Component
public class ReviewRepository {
    private int id;
    private int voto;
    private String descrizione;
    private String emailRecensore;
    private String emailRecensito;

    public int getId() {
        return id;
    }
    public int getVoto() {
        return voto;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public String getEmailRecensore() {
        return emailRecensore;
    }
    public String getEmailRecensito() {
        return emailRecensito;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setVoto(int voto) {
        this.voto = voto;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    public void setEmailRecensore(String emailRecensore) {
        this.emailRecensore = emailRecensore;
    }
    public void setEmailRecensito(String emailRecensito) {
        this.emailRecensito = emailRecensito;
    }


    /**
     * Metodo che ritorna la lista delle recensioni assegnate ad un determinato utente
     *
     * @param emailUtente email dell'utente del quale si vogliono ottenere le recensioni
     * @return lista contenente tutte le recensioni associate ad un determinato utente
     */
    public List<Map<String, Object>> getRecensioni(String emailUtente, JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList("SELECT * FROM reviewsdb WHERE emailRecensito = '"+emailUtente+"'");
    }


    /**
     * Metodo per l'inserimento di una recensione
     *
     * @return true se l'inserimento è stato effettuato, false altrimenti
     */
    public boolean inserimento(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.update("INSERT INTO `reviewsdb` (`id`, `voto`, `descrizione`, `emailRecensore`, `emailRecensito`) " +
                "VALUES (NULL, '" + this.voto + "', '" + this.descrizione + "', '" + this.emailRecensore + "', '" + this.emailRecensito + "')") == 1;
    }
}
