package com.rv.justmeet.utility;

import com.rv.justmeet.exceptions.*;
import com.rv.justmeet.main.parser.EventParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class iOUtility {
    public static Consumer<String> printer = System.out::println;
    public static BufferedReader stringReader = new BufferedReader(new InputStreamReader(System.in));
    public static Scanner scanner = new Scanner(System.in);


    /**
     * Metodo di utility per la scelta della categoria dellìevento che l'utente sta creando
     *
     * @return l'id della categoria
     */
    public static int inserisciCategoriaEvento() {
        int indiceScelta;
        try {
            String response = RequestComunication.getInstance().restRequest("/eventi/getcategorie", "GET");
            List<String> categorie = EventParser.getInstance().parseCategorie(response);

            for (int x = 0; x < categorie.size(); x++)
                printer.accept((x + 1) + ") " + categorie.get(x));
            printer.accept("Inserisci la categoria dell'evento da voler aggiungere: \n");

            if ((indiceScelta = scanner.nextInt()) > categorie.size())
                throw new WrongCategoryException();
        } catch (WrongCategoryException e) {
            printer.accept(e.getMessage());
            return inserisciCategoriaEvento();
        }
        return indiceScelta;
    }

    /**
     * Metodo di utility per l'inserimento di una stringa da parte dell'utente
     *
     * @param campo , nome del campo da inserire
     * @param min   , lunghezza minima della stringa
     * @param max,  lunghezza massima della stringa
     * @return la stringa inserita
     */
    public static String inserisciStringa(String campo, int min, int max) {
        printer.accept("Inserisci " + campo + ": ");
        final String inserted;
        try {
            inserted = getString();
            if ((inserted.length() < min) || (inserted.length() > max))
                throw new WrongCharactersNumber(min, max);
        } catch (WrongCharactersNumber e) {
            printer.accept(e.getMessage());
            return inserisciStringa(campo, min, max);
        }
        return inserted;
    }

    /**
     * Metodo di utility per l'inserimento di una data da parte dell'utente
     *
     * @param richiestaCampo, messaggio contestuale che verrà stampato prima di chiedere l'inserimento della data
     * @return la data inserita dall'utente sotto forma di stringa
     */
    public static String inserisciData(String richiestaCampo) {
        printer.accept(richiestaCampo);
        final String data;
        try {
            data = getString();
            if ((data.length() != 10) || (data.charAt(4) != '-') || (data.charAt(7) != '-')) {
                throw new WrongDataException();
            }
        } catch (WrongDataException e) {
            printer.accept(e.getMessage());
            return inserisciData(richiestaCampo);
        }
        return data;
    }

    /**
     * Metodo di utility per l'inserimento di un orario da parte dell'utente
     *
     * @param tipo , messaggio contestuale relativo al tipo di ora richiesta
     * @return l'ora inserita dall'utente sotto forma di stringa
     */
    public static String inserisciOra(String tipo) {
        printer.accept("Inserire ora " + tipo + ": ");
        final String ora;
        try {
            ora = getString();
            if ((ora.length() != 5) || (ora.charAt(2) != ':')) {
                throw new WrongTimeException();
            }
        } catch (WrongTimeException e) {
            printer.accept(e.getMessage());
            return inserisciOra(tipo);
        }
        return ora + ":00";
    }

    /**
     * Metodo di utility per l'inserimento di un numero con virgola da parte dell'utente
     *
     * @param tipo, messaggio contestuale relativo al tipo di dato che si chiede di inserire
     * @return il numero inserito
     */
    public static float inserisciFloat(String tipo) {
        printer.accept("Inserisci " + tipo + ": ");
        final float toInsert;
        try {
            toInsert = scanner.nextFloat();
            if (toInsert < 0) {
                throw new IOException();
            }
        } catch (IOException e) {
            printer.accept("Il numero deve essere positivo! Riprova");
            return inserisciFloat(tipo);
        }
        return toInsert;
    }

    /**
     * Metodo di utlity per l'inserimento di un numero intero da parte dell'utente
     *
     * @param tipo           ,messaggio contestuale relativo al tipo di dato che si chiede di inserire
     * @param min            , grandezza minima del numero
     * @param max            , grandezza massima del numero
     * @param tipoEccezione, messaggio da stampare in caso di violazione dei parametri.Dipende dal tipo di dato che deve essere inserito
     * @return il numero inserito
     */
    public static int inserisciInt(String tipo, int min, int max, String tipoEccezione) {
        printer.accept("Inserire " + tipo + ": ");
        final int toInsert = scanner.nextInt();
        try {
            if ((toInsert < min) || (toInsert > max))
                throw new WrongEtaException(tipoEccezione);
        } catch (WrongEtaException e) {
            printer.accept(e.getMessage());
            return inserisciInt(tipo, min, max, tipoEccezione);
        }
        return toInsert;
    }

    /**
     * Metodo per effettuare l'inserimento e controllare se lancia eccezioni
     *
     * @return String testo inserito
     */
    public static String getString() {
        try {
            return stringReader.readLine();
        } catch (IOException e) {
            printer.accept(e.getMessage());
            return getString();
        }
    }
}
