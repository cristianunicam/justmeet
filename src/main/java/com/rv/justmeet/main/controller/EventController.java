package com.rv.justmeet.main.controller;

import com.rv.justmeet.exceptions.EventApplyException;
import com.rv.justmeet.exceptions.MaxPartecipantsException;
import com.rv.justmeet.main.core.MySQLConnection;
import com.rv.justmeet.main.event.EventsDisplayer;
import com.rv.justmeet.main.event.EventsManager;
import com.rv.justmeet.main.user.LoggedUser;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.rv.justmeet.main.core.SoftwareManager.printer;

public abstract class EventController {
    public static final String[] campiEvento = {
            "ID evento","Categoria","Titolo","Descrizione","Citta'","Via","Data","Ora inizio","Ora fine","Prezzo","Numero massimo partecipanti","Email organizzatore"
    };
    public static final String[] campiEventoModificabili = {
            "Titolo","Descrizione","Citta'","Via","Data","Ora inizio","Ora fine","Prezzo","Numero massimo partecipanti"
    };
    public static final String[] campiDatabaseModificabili = {
            "titolo","descrizione","citta","via","data","oraInizio","oraFine","prezzo","maxPartecipanti"
    };

    /**
     * Metodo che esegue la query per l'agginta di un evento nel database
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
     * @return
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
     */
    public static boolean annullaPartecipazione(final int idEvento) {
        return MySQLConnection.getInstance().insertQuery(
                "DELETE FROM `partecipantsdb` " +
                        "WHERE idEvento = " + idEvento +
                        " AND emailUtente = \"" + LoggedUser.getInstance().getEmail() + "\""
        );
    }


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
     */
    public static void annullaEvento(final String emailUtente, final int idEvento){
        MySQLConnection.getInstance().insertQuery(
                "DELETE FROM partecipantsdb " +
                        "WHERE idEvento = "+idEvento
        );

        MySQLConnection.getInstance().insertQuery(
                "DELETE FROM eventsdb "+
                        "WHERE emailOrganizzatore = \""+emailUtente+"\""+
                        "AND id = "+idEvento
        );

        MySQLConnection.getInstance().insertQuery(
                "SET  @num := 0 " +
                        "UPDATE eventsdb SET id = @num := (@num+1) " +
                        "ALTER TABLE eventsdb AUTO_INCREMENT =1"
        );
    }


    public static boolean getPartecipazione(final String emailUtente, final int idEvento){
        try {
            return MySQLConnection.getInstance().selectQueryReturnSet(
                    "SELECT *" +
                            " FROM partecipantsdb" +
                            " WHERE idEvento = " + idEvento +
                            "AND emailUtente = \"" + emailUtente + "\""
            ).next();
        }catch (SQLException e){
            printer.accept(e.getMessage());
            return false;
        }
    }
}
