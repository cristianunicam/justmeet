package com.rv.justmeet.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EventRepository {
    private int categoria;
    private String titolo;
    private String descrizione;
    private String citta;
    private String via;
    private String data;
    private String oraInizio;
    private String oraFine;
    private float prezzo;
    private int maxPartecipanti;
    private String emailOrganizzatore;

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setOraInizio(String oraInizio) {
        this.oraInizio = oraInizio;
    }

    public void setOraFine(String oraFine) {
        this.oraFine = oraFine;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public void setMaxPartecipanti(int maxPartecipanti) {
        this.maxPartecipanti = maxPartecipanti;
    }

    public void setEmailOrganizzatore(String email) {
        this.emailOrganizzatore = email;
    }


    /**
     * Metodo per la registrazione di un utente tramite post
     *
     * @return <code>true</code> se l'evento è stato aggiunto, <code>false</code> altrimenti
     */
    public boolean inserimento(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.update("INSERT INTO `eventsdb` (`id`, `categoria`, `titolo`, `descrizione`, `citta`, `via`, `data`, `oraInizio`, `oraFine`, `prezzo`, `maxPartecipanti`, `emailOrganizzatore`) " +
                "VALUES (NULL, " + this.categoria + ", '" + this.titolo + "', '" + this.descrizione + "', '" + this.citta + "', '" + this.via + "', '" + this.data + "', '" + this.oraInizio + "', '" + this.oraFine + "', " + this.prezzo + ", " + this.maxPartecipanti + ", '" + this.emailOrganizzatore + "')") == 1;
    }


    /**
     * Metodo per la modifica di un evento tramite post
     *
     * @param payload dati dell'evento da voler modificare
     * @return <code>true</code> se l'evento è stato modificato, <code>false</code> altrimenti
     */
    public boolean modifica(JdbcTemplate jdbcTemplate, Map<String,Object> payload){
        return jdbcTemplate.update("UPDATE `eventsdb` " +
                "SET " + payload.get("nomeCampo") + " = '" + payload.get("campoModificato") + "' " +
                "WHERE id = " + payload.get("idEvento")) == 1;
    }


    /**
     * Metodo per ottenere i dati di tutti gli eventi presenti
     *
     * @return lista dei dati degli eventi presenti
     */
    public List<Map<String, Object>> getEventi(JdbcTemplate jdbcTemplate){
        return jdbcTemplate.queryForList("SELECT * FROM `eventsdb`");
    }


    /**
     * Metodo per ritornare i dati di un determinato evento
     *
     * @param id id dell'evento da voler ritornare al client che ne fa richiesta
     * @return lista contente i dati dell'evento
     */
    public List<Map<String,Object>> getEvento(int id, JdbcTemplate jdbcTemplate) {
        List<Map<String, Object>> datiEvento = jdbcTemplate.queryForList("SELECT * FROM `eventsdb` WHERE id = "+id);
        if(datiEvento.isEmpty())
            datiEvento.add(0,Map.of("success",Boolean.FALSE));
        return datiEvento;
    }


    /**
     * Metodo che ritorna al client la lista degli eventi da lui pubblicati
     *
     * @param emailUtente email dell'utente al quale ritornare gli eventi pubblicati
     * @return lista degli eventi di un dato utente
     */
    public List<Map<String, Object>> getEventiPubblicati(String emailUtente, JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList("SELECT eventsdb.id,categoriesdb.nome AS categoria,titolo,descrizione,citta,data,prezzo " +
                "FROM `eventsdb` "+
                "JOIN `categoriesdb` ON categoria = categoriesdb.id "+
                "WHERE eventsdb.emailOrganizzatore = \""+emailUtente+"\""
        );
    }


    /**
     * Metodo per ritornare al client la lista degli eventi al quale un dato utente partecipa
     *
     * @param emailUtente email dell'utente del quale ritornare gli eventi
     * @return lista degli eventi ai quali un dato utente partecipa
     */
    public List<Map<String,Object>> getEventiComePartecipante(String emailUtente, JdbcTemplate jdbcTemplate){
        return jdbcTemplate.queryForList("SELECT eventsdb.id,categoriesdb.nome AS categoria,titolo,descrizione,citta,data,prezzo " +
                "FROM `eventsdb` "+
                "JOIN `categoriesdb` ON categoria = categoriesdb.id "+
                "JOIN `partecipantsdb` ON idEvento = eventsdb.id "+
                "WHERE partecipantsdb.emailUtente = \""+emailUtente+"\"");
    }


    /**
     * Metodo per annullare un evento
     *
     * @param idEvento id dell'evento che si vuole annullare
     * @param emailUtente email dell'utente che vuole annullare l'evento
     */
    public Boolean annullaEvento(JdbcTemplate jdbcTemplate, String emailUtente,final int idEvento) {
        jdbcTemplate.update(
                "DELETE FROM partecipantsdb " +
                "WHERE idEvento = "+idEvento);
        if(jdbcTemplate.update(
                "DELETE FROM eventsdb "+
                        "WHERE emailOrganizzatore = '"+emailUtente+"'"+
                        " AND id = "+idEvento
        ) != 1)
            return false;

        jdbcTemplate.update("SET @num := 0; ");
        jdbcTemplate.update("UPDATE eventsdb SET id = @num := (@num+1);");
        jdbcTemplate.update("ALTER TABLE eventsdb AUTO_INCREMENT = 1");
        return true;
    }


    /**
     * Metodo per ritornare al client la lista delle categorie nel quale un evento può essere registrato
     *
     * @return lista delle possibili categorie di un evento
     */
    public List<Map<String, Object>> getCategorie(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList("SELECT * FROM categoriesdb");
    }


    /**
     * Metodo per controllare che l'utente è l'organizzatore di un dato evento
     *
     * @param jdbcTemplate classe utilizzata per effettuare query al database
     * @param emailUtente email dell'utente che si vuole verificare sia l'organizzatore del dato evento
     * @param idEvento evento del quale controllare se si è l'organizzatore
     * @return true se l'utente è l'organizzatore dell'evento, false altrimenti
     */
    public Boolean isPartecipante(JdbcTemplate jdbcTemplate, String emailUtente,final int idEvento) {
        return !jdbcTemplate.queryForList("SELECT emailUtente " +
                "FROM `partecipantsdb` " +
                "WHERE idEvento = " + idEvento +
                " AND emailUtente = '" + emailUtente + "'").isEmpty();
    }


    /**
     * Metodo per controllare che l'utente è l'organizzatore di un dato evento
     *
     * @param jdbcTemplate classe utilizzata per effettuare query al database
     * @param emailUtente email dell'utente che si vuole verificare sia l'organizzatore del dato evento
     * @param idEvento evento del quale controllare se si è l'organizzatore
     * @return true se l'utente è l'organizzatore dell'evento, false altrimenti
     */
    public Boolean isOrganizzatore(JdbcTemplate jdbcTemplate, String emailUtente,final int idEvento) {
        return !jdbcTemplate.queryForList("SELECT emailOrganizzatore " +
                "FROM `eventsdb` " +
                "WHERE id = " + idEvento +
                " AND emailOrganizzatore = '" + emailUtente + "'"
        ).isEmpty();
    }

    public List<Map<String, Object>> getPartecipanti(JdbcTemplate jdbcTemplate,final int idEvento) {
        return jdbcTemplate.queryForList("SELECT emailUtente FROM partecipantsdb WHERE idEvento = "+idEvento);
    }
}
