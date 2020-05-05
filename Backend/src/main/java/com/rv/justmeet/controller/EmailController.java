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

@EnableScheduling
@Component
public class EmailController {
    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 0 0 * * *")
    public void emailSender(){
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
                getEmailDaNotificare(dataDomani)
        );
        aggiornaFlag(dataDomani);
    }


    private void sendEmail(List<Map<String, Object>> listaEmailDaNotificare) {
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
                msg.setSubject("L'evento per domani al quale partecipi è stato confermato!");
                // msg.setContent("TEST EMAIL", "text/html");
                msg.setSentDate(new Date());

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent("L'evento al quale partecipi è stato confermato!", "text/html");

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


    private List<Map<String, Object>> getEmailDaNotificare(LocalDate dataDomani) {
        return jdbcTemplate.queryForList("SELECT emailUtente FROM partecipantsdb " +
                "JOIN eventsdb ON partecipantsdb.idEvento = eventsdb.id " +
                "WHERE eventsdb.confermato = 0 " +
                "AND eventsdb.data = '"+dataDomani.toString()+"' " +
                "AND eventsdb.minPartecipanti < (SELECT COUNT(DISTINCT idEvento) " +
                "                                WHERE partecipantsdb.idEvento = eventsdb.id)");
    }

    private void aggiornaFlag(LocalDate dataDomani){
        jdbcTemplate.update("UPDATE eventsdb " +
                "SET chiuso = 1 " +
                "WHERE chiuso = 0 " +
                "AND data = '"+dataDomani+"'");
    }
}