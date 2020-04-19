package com.rv.justmeet.main.event;

import com.rv.justmeet.exceptions.*;
import com.rv.justmeet.main.core.MySQLConnection;
import com.rv.justmeet.main.user.LoggedUser;
import com.rv.justmeet.utility.iOUtility;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.rv.justmeet.main.core.SoftwareManager.printer;
import static com.rv.justmeet.main.core.SoftwareManager.scanner;


/**
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 *
 * Classe Singleton che gestisce le azioni relaive agli eventi
 */
public class EventsManager {
    private static EventsManager instance = null;

    private EventsManager(){}

    public static EventsManager getInstance(){
        if(instance == null)
            instance = new EventsManager();
        return instance;
    }

    /**
     * Permette di creare un evento che verra' inserito in bacheca e salvato nel database
     */
    public void aggiungiEvento(){
        final int categoria = iOUtility.inserisciCategoriaEvento();
        final String titolo = iOUtility.inserisciStringa("titolo" , 10 , 50 );
        final String descrizione = iOUtility.inserisciStringa("descrizione" , 15 , 500);
        final String citta = iOUtility.inserisciStringa("citta" , 3 , 30);
        final String via = iOUtility.inserisciStringa("via" , 4 , 30);
        final String data = iOUtility.inserisciData("Inserisci la data dell'evento nella forma AAAA-MM-DD: ");
        final String oraInizio = iOUtility.inserisciOra("inizio");
        final String oraFine = iOUtility.inserisciOra("fine");
        final float prezzo = iOUtility.inserisciFloat("prezzo");
        final int maxPartecipanti = iOUtility.inserisciInt(
                "numero massimo partecipanti", 2, 100000, "Il numero di partecipanti deve essere compreso tra 2 e 100000"
        );
        MySQLConnection.getInstance().insertQuery(
                "INSERT INTO `eventsdb` (`id`, `categoria`, `titolo`, `descrizione`, `citta`, `via`, `data`, `oraInizio`, `oraFine`, `prezzo`, `maxPartecipanti`, `emailOrganizzatore`) " +
                        "VALUES (NULL, '"+categoria+"', '"+titolo+"', '"+descrizione+"', '"+citta+"', '"+via+"', '"+data+"', '"+oraInizio+"', '"+oraFine+"', '"+prezzo+"', '"+maxPartecipanti+"', '"+LoggedUser.getInstance().getEmail()+"');");
    }

    /**
     * Permette di scegliere un evento a cui partecipare. Salvando poi la partecipazione nel database
     */
    public void partecipaEvento(){
        printer.accept("Inserisci l'ID dell'evento al quale vuoi partecipare (0 per annullare): ");
        int eventoScelto;
        if((eventoScelto = scanner.nextInt()) == 0)
            return;
        try {
            int maxPartecipanti = getMaxPartecipantiEvento(eventoScelto);
            int partecipantiEsistenti = countPartecipantiEvento(eventoScelto);
            if(partecipantiEsistenti >= maxPartecipanti)
                throw new MaxPartecipantsException();
        }catch (EventApplyException | SQLException e){
            printer.accept(e.getMessage());
            partecipaEvento();
        }catch (MaxPartecipantsException e){
            printer.accept(e.getMessage());
            return;
        }
        MySQLConnection.getInstance().insertQuery(
                "INSERT INTO partecipantsdb (emailUtente,idEvento)\n" +
                "SELECT userdb.email,eventsdb.id\n" +
                "FROM userdb,eventsdb\n" +
                "WHERE userdb.email = \""+ LoggedUser.getInstance().getEmail() +"\" AND eventsdb.id = "+eventoScelto);
        printer.accept("Partecipazione effettuata!");
    }

    /**
     * Effettua la select per ottenere il massimo numero di partecipanti ad un evento
     *
     * @param eventoScelto id dell'evento del quale si vuole sapere il numero massimo di partecipanti
     * @return massimo numero di partecipanti
     * @throws SQLException non è stato possibile ottenere il numero di partecipanti all'evento
     * @throws EventApplyException se non è stato trovato il numero massimo di partecipanti all'evento
     */
    private int getMaxPartecipantiEvento(int eventoScelto) throws SQLException,EventApplyException{
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
    private int countPartecipantiEvento(int eventoScelto) throws SQLException {
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


}