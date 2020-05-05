package com.rv.justmeet.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Classe controller per le email, utilizzata per notificare gli utenti
 *
 * @author Cristian Verdecchia Lorenzo Romagnoli
 */
@EnableScheduling
@Component
public class EmailController {
    @Autowired
    public JdbcTemplate jdbcTemplate;

    /**
     * Metodo che viene avviato ogni giorno a mezzanotte il quale controlla se vi sono eventi che scadranno il giorno
     * seguente, in modo da confermare l'evento se viene raggiunto il minimo numero di partecipanti
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledSender(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        LocalDate dataDomani = null;

        try {
            dataDomani = LocalDate.now().plusDays(1);
            format.parse(dataDomani.toString());
        }catch (ParseException e){
            System.out.println("ERRORE NEL PARSE DELLA DATA");
            System.exit(-1);
        }

        sendEmail(
                getEmailDaNotificare(dataDomani), "L'evento per domani al quale partecipi è stato confermato!"
        );
        aggiornaFlag(dataDomani);
    }


    /**
     * Metodo utilizzato per richiamare il sendEmail, permettendo quindi l'invio di mail
     *
     * @param listaEmailDaNotificare lista degli indirizzi mail degli utenti da notificare
     * @param testoDaInviare testo da inviare come notifica a tutti gli indirizzi email
     */
    public static void notificaUtenti(List<Map<String,Object>> listaEmailDaNotificare, String testoDaInviare){
        sendEmail(listaEmailDaNotificare,testoDaInviare);
    }


    /**
     * Metodo per l'invio di mail a tutti gli indirizzi email contenuti nella lista passata,
     * il testo contenuto all'interno della mail sarà il testo passato "<code>testoDaInviare</code>"
     *
     * @param listaEmailDaNotificare lista degli indirizzi email da notificare
     * @param testoDaInviare testo che verrà inviato agli indirizzi email
     */
    private static void sendEmail(List<Map<String, Object>> listaEmailDaNotificare, String testoDaInviare) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("justmeetrv@gmail.com", "justmeetRV20");
            }
        });

        try {
            for (Map<String, Object> stringObjectMap : listaEmailDaNotificare) {
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress("justmeetrv@gmail.com", false));
                msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(stringObjectMap.get("emailUtente").toString()));
                msg.setSubject("Notifica eventi - JustMeet");
                // msg.setContent("TEST EMAIL", "text/html");
                msg.setSentDate(new Date());

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(testoDaInviare, "text/html");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                MimeBodyPart attachPart = new MimeBodyPart();
                //SENZA QUESTO NON FUNZIA
                attachPart.setText(" ");
                multipart.addBodyPart(attachPart);
                msg.setContent(multipart);
                Transport.send(msg);
            }
        }catch (MessagingException e){
            System.out.println("Errore durante l'invio della notifica tramite mail!");
        }
    }


    /**
     * Metodo che ritorna tutti gli indirizzi email che devono essere notificati
     * dell'avvenuta conferma di un evento
     *
     * @param dataDomani data del giorno successivo al giorno di utilizzo
     * @return lista contente gli indirizzi email degli utenti da dover modificare
     */
    private List<Map<String, Object>> getEmailDaNotificare(LocalDate dataDomani) {
        return jdbcTemplate.queryForList("SELECT emailUtente FROM partecipantsdb " +
                "JOIN eventsdb ON partecipantsdb.idEvento = eventsdb.id " +
                "WHERE eventsdb.confermato = 0 " +
                "AND eventsdb.data = '"+dataDomani.toString()+"' " +
                "AND eventsdb.minPartecipanti < (SELECT COUNT(DISTINCT idEvento) " +
                "                                WHERE partecipantsdb.idEvento = eventsdb.id)");
    }

    /**
     * Metodo che aggiorna il flag chiuso di un evento in modo che non venga più mostrato nella bacheca
     * in quanto l'evento ha raggiunto un numero sufficiente di partecipanti
     *
     * @param dataDomani data del giorno successivo per controllare nel database se ci sono
     *                   eventi che devono essere modificati
     */
    private void aggiornaFlag(LocalDate dataDomani){
        jdbcTemplate.update("UPDATE eventsdb " +
                "SET chiuso = 1 " +
                "WHERE chiuso = 0 " +
                "AND data = '"+dataDomani+"'");
    }
}