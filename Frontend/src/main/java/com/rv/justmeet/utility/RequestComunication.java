package com.rv.justmeet.utility;

import com.rv.justmeet.main.core.BackendConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.rv.justmeet.utility.iOUtility.printer;


public class RequestComunication {
    private static RequestComunication instance = null;
    private HttpURLConnection con;
    private URL url;
    private BufferedReader reader;

    private RequestComunication() {
    }

    /**
     * Ritorna l'istanza di questa classe
     *
     * @return istanza della classe
     */
    public static RequestComunication getInstance() {
        if (instance == null)
            instance = new RequestComunication();
        return instance;
    }

    /**
     * Consente di effettuare una chiamata http usando i metodi get;
     *
     * @param path   il path univoco per fare la chiamata rest
     * @param method il metodo http da applicare
     * @return ritorna la risposta dal server
     */
    public String restRequest(String path, String method) {
        String dataSite = null;

        try {
            this.url = new URL(BackendConnection.getInstance().getDomain() + path);
            this.con = (HttpURLConnection) url.openConnection();
            this.con.setRequestMethod(method);

            this.reader = new BufferedReader(new InputStreamReader(this.con.getInputStream()));
            dataSite = this.reader.readLine();
            this.con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataSite;
    }

    /**
     * Consente di effettuare un metodo post per inviare dati al server
     *
     * @param path       indirizzo al quale effettuare la richiesta
     * @param method     tipo di metodo http da eseguire
     * @param parameters parametri da inviare DA SCRIVERE SOTTO DOMRA DI STRINGA JSON
     */
    public String restSend(String path, String method, String parameters) {
        printer.accept("PARAMETRI: " + parameters);
        //prima parte per l'invio dei parametri
        try {
            this.url = new URL(BackendConnection.getInstance().getDomain() + path);
            byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
            this.con = (HttpURLConnection) this.url.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod(method);
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(postData);

            //seconda parte per la lettura del risultato
            this.reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String responseLine;
            StringBuilder content = new StringBuilder();

            while ((responseLine = this.reader.readLine()) != null) {
                //se non va provare questo
                //content.append(responseline.trim());
                content.append(responseLine);
                content.append(System.lineSeparator());
            }
            return content.toString();
            //risposta , da mettere ciò che serve e togliere il println
        } catch (IOException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        } finally {
            con.disconnect();
        }
        return null;
    }
}

