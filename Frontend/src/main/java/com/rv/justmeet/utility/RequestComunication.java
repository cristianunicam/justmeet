package com.rv.justmeet.utility;

import com.rv.justmeet.main.core.BackendConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.rv.justmeet.utility.IOUtility.printer;

/**
 * @author Lorenzo Romagnoli, Cristian Verdecchia
 *
 * Classe utilizzata per effetuare richieste REST sul server
 */
public class RequestComunication implements RequestComunicationInterface{
    private static RequestComunication instance = null;

    private RequestComunication() { }

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


    public String restRequest(String path, String method, String parameters) {
        try {
            URL url = new URL(BackendConnection.getInstance().getDomain() + path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);

            if(parameters != null) {
                con.setDoOutput(true);
                byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
                con.setRequestProperty("User-Agent", "Java client");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.write(postData);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String responseLine;
            StringBuilder content = new StringBuilder();

            while ((responseLine = reader.readLine()) != null) {
                content.append(responseLine);
                content.append(System.lineSeparator());
            }
            con.disconnect();

            return content.toString();
        } catch (IOException e) {
            printer.accept(e.getMessage());
            System.exit(-1);
        }
        return null;
    }
}

