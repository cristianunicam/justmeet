package com.rv.justmeet.main.controller;

import com.rv.justmeet.exceptions.EventApplyException;
import com.rv.justmeet.exceptions.MaxPartecipantsException;
import com.rv.justmeet.main.core.MySQLConnection;
import com.rv.justmeet.main.event.EventsManager;
import com.rv.justmeet.main.user.LoggedUser;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.rv.justmeet.utility.iOUtility.printer;

public abstract class EventController {
    public static final String[] campiEvento = {
            "Categoria","Titolo","Descrizione","Citta'","Via","Data","Ora inizio","Ora fine","Prezzo","Numero massimo partecipanti","Email organizzatore"
    };
    public static final String[] campiEventoModificabili = {
            "Titolo","Descrizione","Citta'","Via","Data","Ora inizio","Ora fine","Prezzo","Numero massimo partecipanti"
    };
    public static final String[] campiDatabaseModificabili = {
            "titolo","descrizione","citta","via","data","oraInizio","oraFine","prezzo","maxPartecipanti"
    };


    /**
     * Metodo che esegue la query per l'agginta di un evento nel database
     * @param categoria categoria dell'evento da voler aggiungere
     * @param titolo titolo dell'evento da voler aggiungere
     * @param descrizione descrizione dell'evento da voler aggiungere
     * @param citta citta dell'evento da voler aggiungere
     * @param via via dell'evento da voler aggiungere
     * @param data data dell'evento da voler aggiungere
     * @param oraInizio Ora di inizio dell'evento da voler aggiungere
     * @param oraFine Ora di fine dell'evento da voler aggiungere
     * @param prezzo prezzo dell'evento da voler aggiungere
     * @param maxPartecipanti numero massimo di partecipanti all'evento da voler aggiungere
     * @param email email dell'organizzatore dell'evento da voler aggiungere
     */
    public static void aggiungiEvento(
            final int categoria,final String titolo,final String descrizione,final String citta,final String via,final String data,
            final String oraInizio,final String oraFine,final float prezzo,final int maxPartecipanti,String email
    ){
        MySQLConnection.getInstance().insertQuery(
                "INSERT INTO `eventsdb` (`id`, `categoria`, `titolo`, `descrizione`, `citta`, `via`, `data`, `oraInizio`, `oraFine`, `prezzo`, `maxPartecipanti`, `emailOrganizzatore`) " +
                        "VALUES (NULL, '"+categoria+"', '"+titolo+"', '"+descrizione+"', '"+citta+"', '"+via+"', '"+data+"', '"+oraInizio+"', '"+oraFine+"', '"+prezzo+"', '"+maxPartecipanti+"', '"+ email+"');");
    }


    /**
     * Metodo per eseguire la query per la partecipazione di un utente ad un evento
     *
     * @param eventoScelto id evento al quale si vuole partecipare
     * @return <code>true</code> se la partecipazione è stata registrata,
     *         <code>false</code> se si è raggiunto il limite massimo di partecipanti
     */
    public static boolean partecipaEvento(final int eventoScelto){
        try {
            int maxPartecipanti = getMaxPartecipantiEvento(eventoScelto);
            int partecipantiEsistenti = countPartecipantiEvento(eventoScelto);
            if(partecipantiEsistenti >= maxPartecipanti)
                throw new MaxPartecipantsException();
        }catch (EventApplyException | SQLException e){
            printer.accept(e.getMessage());
            EventsManager.getInstance().partecipaEvento(eventoScelto);
        }catch (MaxPartecipantsException e){
            printer.accept(e.getMessage());
            return false;
        }

        MySQLConnection.getInstance().insertQuery(
                "INSERT INTO partecipantsdb (emailUtente,idEvento)\n" +
                        "SELECT userdb.email,eventsdb.id\n" +
                        "FROM userdb,eventsdb\n" +
                        "WHERE userdb.email = \""+ LoggedUser.getInstance().getEmail() +"\" AND eventsdb.id = "+eventoScelto);
        return true;
    }


    /**
     * Effettua la select per ottenere il massimo numero di partecipanti ad un evento
     *
     * @param eventoScelto id dell'evento del quale si vuole sapere il numero massimo di partecipanti
     * @return massimo numero di partecipanti
     * @throws SQLException non è stato possibile ottenere il numero di partecipanti all'evento
     * @throws EventApplyException se non è stato trovato il numero massimo di partecipanti all'evento
     */
    private static int getMaxPartecipantiEvento(final int eventoScelto) throws SQLException,EventApplyException{
        int maxPartecipanti;
        //Prendo il numero massimo di partecipanti dal database
        ResultSet maxPartecipantiResultSet = MySQLConnection.getInstance().selectQueryReturnSet("SELECT maxPartecipanti FROM eventsdb WHERE id = " + eventoScelto);

        //Controllo che sia prensente almeno un risultato
        if (maxPartecipantiResultSet.next()) {
            //Trasformo il risultato in intero se i partecipanti sono 0, non è stato possibile ottenere i partecipanti dal database
            if ((maxPartecipanti = maxPartecipantiResultSet.getInt("maxPartecipanti")) <= 0)
                throw new SQLException();
        }else
            throw new EventApplyException();
        return maxPartecipanti;
    }


    /**
     * Conta il numero di partecipanti ad un dato evento
     *
     * @param eventoScelto id dell'evento del quale si vuole sapere il numero di partecipanti
     * @return numero di partecipanti all'evento
     * @throws SQLException errore nel caso in cui non vi sono partecipanti all'evento
     */
    private static int countPartecipantiEvento(final int eventoScelto) throws SQLException {
        //Prendo il numero di partecipanti all'evento
        ResultSet partecipantiEsistentiResultSet = MySQLConnection.getInstance().selectQueryReturnSet(
                "SELECT COUNT(idEvento) \n" +
                        "FROM partecipantsdb \n" +
                        "WHERE idEvento = "+eventoScelto);
        //Prendo il numero di utenti che già partecipano al dato evento
        if(!partecipantiEsistentiResultSet.next())
            throw new SQLException();
        return partecipantiEsistentiResultSet.getInt("COUNT(idEvento)");
    }


    /**
     * Metodo per annullare la partecipazione di un utente ad un evento
     *
     * @param idEvento id dell'evento del quale si vuole annullare la partecipazione
     * @return <code>true</code> se l'annullamento è avvenuto, <code>false</code> altrimenti
     */
    public static boolean annullaPartecipazione(final int idEvento) {
        return MySQLConnection.getInstance().insertQuery(
                "DELETE FROM `partecipantsdb` " +
                        "WHERE idEvento = " + idEvento +
                        " AND emailUtente = \"" + LoggedUser.getInstance().getEmail() + "\""
        );
    }


    /**
     * Metodo per la modifica di un campo dell'evento
     *
     * @param nomeCampo nome del campo da voler modificare
     * @param campoModificato campo modificato da voler cambiare all'interno del database
     * @param idEvento id dell'evento del quale si vuole modificare il campo
     */
    public static void modificaEvento(final String nomeCampo , final String campoModificato , final int idEvento){
        MySQLConnection.getInstance().insertQuery(
                "UPDATE `eventsdb` \n" +
                        "SET "+nomeCampo+" = "+campoModificato+
                        "WHERE id = "+idEvento
        );

        printer.accept("L'evento è stato modificato!");
    }


    /**
     * Metodo per annullare un evento
     *
     * @param emailUtente email dell'utente che vuole annullare l'evento
     * @param idEvento id dell'evento che si vuole annullare
     */
    public static void annullaEvento(final String emailUtente, final int idEvento){
        MySQLConnection.getInstance().insertQuery(
                "DELETE FROM partecipantsdb " +
                        "WHERE idEvento = "+idEvento
        );

        MySQLConnection.getInstance().insertQuery(
                "DELETE FROM eventsdb "+
                        "WHERE emailOrganizzatore = \""+emailUtente+"\""+
                        " AND id = "+idEvento
        );

        MySQLConnection.getInstance().insertQuery("SET @num := 0; ");
        MySQLConnection.getInstance().insertQuery("UPDATE eventsdb SET id = @num := (@num+1);");
        MySQLConnection.getInstance().insertQuery("ALTER TABLE eventsdb AUTO_INCREMENT = 1");
    }


    /**
     * Metodo per vedere se l'utente partecipa all'evento
     *
     * @param emailUtente dell'utente del quale si vuole controllare la partecipazione
     * @param idEvento dell'evento del quale si vuole controllare se l'utente è un partecipante
     * @return <code>true</code> se l'utente partecipa all'evento, <code>false</code> altrimenti
     */
    public static boolean getPartecipazione(final String emailUtente, final int idEvento){
        return MySQLConnection.getInstance().selectQuery(
                "SELECT *" +
                        " FROM partecipantsdb" +
                        " WHERE idEvento = " + idEvento +
                        " AND emailUtente = \"" + emailUtente + "\""
        );
    }


    /**
     * Metodo per ottenere i dati di un evento dal database
     *
     * @param idEventoDaMostrare id dell'evento del quale ottenere i dati
     * @return Set di dati relativi all'evento
     */
    public static ResultSet getEvento(final int idEventoDaMostrare){
       return MySQLConnection.getInstance().selectQueryReturnSet(
                "SELECT categoriesdb.nome,titolo,descrizione,citta,via,data,oraInizio,oraFine,prezzo,maxPartecipanti,emailOrganizzatore " +
                        "FROM `eventsdb` JOIN `categoriesdb`ON categoria = categoriesdb.id " +
                        "WHERE eventsdb.id = "+idEventoDaMostrare
        );
    }


    public static int getMaxIdEvento(){
        ResultSet evento = MySQLConnection.getInstance().selectQueryReturnSet(
                "SELECT MAX(id) " +
                        "FROM eventsdb"
        );

        try {
            evento.next();
            return evento.getInt("id");
        }catch (SQLException e){
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return 0;
    }
}
