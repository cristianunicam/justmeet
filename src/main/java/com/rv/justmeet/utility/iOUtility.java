package com.rv.justmeet.utility;

import com.rv.justmeet.exceptions.*;
import com.rv.justmeet.main.core.MySQLConnection;
import com.rv.justmeet.main.core.SoftwareManager;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.rv.justmeet.main.core.SoftwareManager.printer;
import static com.rv.justmeet.main.core.SoftwareManager.scanner;

public final class iOUtility {
    /**
     * Metodo di utility per la scelta della categoria dellìevento che l'utente sta creando
     *
     * @return l'id della categoria
     */
    public static int inserisciCategoriaEvento() {
        printer.accept("Inserisci la categoria dell'evento da voler aggiungere: \n");
        int indiceScelta;
        try {
            ResultSet categorie = MySQLConnection.getInstance().selectQueryReturnSet("SELECT nome FROM `categoriesdb`");
            int cont = 0;

            while (categorie.next()) {
                cont++;
                printer.accept(
                        cont+") "+categorie.getString("nome")
                );
            }
            if((indiceScelta = scanner.nextInt()) > cont)
                throw new WrongCategoryException();
        }catch (SQLException | WrongCategoryException e){
            printer.accept(e.getMessage());
            return inserisciCategoriaEvento();
        }
        return indiceScelta;
    }

    /**
     * Metodo di utility per l'inserimento di una stringa da parte dell'utente
     *
     * @param campo , nome del campo da inserire
     * @param min , lunghezza minima della stringa
     * @param max, lunghezza massima della stringa
     * @return la stringa inserita
     */
    public static String inserisciStringa(String campo , int min , int max) {
        printer.accept("Inserisci la "+campo+" dell'evento da voler aggiungere: ");
        final String inserted;
        try {
            inserted = SoftwareManager.getString();
            if ((inserted.length() < min)||(inserted.length() > max))
                throw new WrongCharactersNumber(min,max);
        }catch (WrongCharactersNumber e){
            printer.accept(e.getMessage());
            return inserisciStringa(campo , min , max);
        }
        return inserted;
    }

    /**
     * Metodo di utility per l'inserimento di una data da parte dell'utente
     *
     * @param richiestaCampo, messaggio contestuale che verrà stampato prima di chiedere l'inserimento della data
     * @return la data inserita dall'utente sotto forma di stringa
     */
    public static String inserisciData(String richiestaCampo){
        printer.accept(richiestaCampo);
        final String data;
        try{
            data = SoftwareManager.getString();
            if((data.length() != 10) || (data.charAt(4) != '-') || (data.charAt(7) != '-')){
                throw new WrongDataException();
            }
        }catch(WrongDataException e){
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
            ora = SoftwareManager.getString();
            if ((ora.length() != 5) || (ora.charAt(2) != ':')) {
                throw new WrongTimeException();
            }
        } catch (WrongTimeException e) {
            printer.accept(e.getMessage());
            return inserisciOra(tipo);
        }
        return ora+":00";
    }

    /**
     * Metodo di utility per l'inserimento di un numero con virgola da parte dell'utente
     *
     * @param tipo, messaggio contestuale relativo al tipo di dato che si chiede di inserire
     * @return il numero inserito
     */
    public static float inserisciFloat(String tipo){
        printer.accept("Inserisci "+tipo+": ");
        final float toInsert;
        try{
            toInsert = scanner.nextFloat();
            if(toInsert < 0){
                throw new IOException();
            }
        }catch(IOException e){
            printer.accept("Il numero deve essere positivo! Riprova");
            return inserisciFloat(tipo);
        }
        return toInsert;
    }

    /**
     * Metodo di utlity per l'inserimento di un numero intero da parte dell'utente
     *
     * @param tipo ,messaggio contestuale relativo al tipo di dato che si chiede di inserire
     * @param min , grandezza minima del numero
     * @param max , grandezza massima del numero
     * @param tipoEccezione, messaggio da stampare in caso di violazione dei parametri.Dipende dal tipo di dato che deve essere inserito
     * @return il numero inserito
     */
    public static int inserisciInt(String tipo , int min , int max , String tipoEccezione){
        printer.accept("Inserire "+tipo+": ");
        final int toInsert = scanner.nextInt();
        try{
            if((toInsert < min) || (toInsert > max))
                throw new WrongEtaException(tipoEccezione);
        }catch (WrongEtaException e){
            printer.accept(e.getMessage());
            return inserisciInt(tipo,min,max,tipoEccezione);
        }
        return toInsert;
    }
}
