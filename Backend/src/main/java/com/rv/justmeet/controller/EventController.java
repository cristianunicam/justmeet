package com.rv.justmeet.controller;

import com.rv.justmeet.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Classe controller di un evento
 *
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 */
@RestController
@RequestMapping(path="/eventi")
public class EventController {
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    private EventRepository eventi;


    @RequestMapping(path="/inserimento", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Map<String, Boolean> pubblicaEvento(@RequestBody EventRepository evento){
        return Collections.singletonMap("success",evento.inserimento(jdbcTemplate));
    }

    @RequestMapping(path = "/modifica", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Boolean> modifica(@RequestBody Map<String,Object> payload){
        return Collections.singletonMap("success", eventi.modifica(jdbcTemplate,payload));
    }

    @GetMapping(value = "/geteventi", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> getEventi(){
        return eventi.getEventi(jdbcTemplate);
    }

    @GetMapping(value = "/getevento/{idevento}")
    @ResponseBody
    public List<Map<String, Object>> getEvento(@PathVariable("idevento") int idEvento){
        return eventi.getEvento(idEvento,jdbcTemplate);
    }

    @GetMapping(value = "/geteventipubblicati/{emailutente}")
    @ResponseBody
    public List<Map<String,Object>> getEventiPubblicati(@PathVariable("emailutente") String emailUtente){
        return eventi.getEventiPubblicati(emailUtente,jdbcTemplate);
    }

    @GetMapping(value = "/geteventipartecipante/{emailutente}")
    @ResponseBody
    public List<Map<String,Object>> getEventiComePartecipante(@PathVariable("emailutente") String emailUtente){
        return eventi.getEventiComePartecipante(emailUtente,jdbcTemplate);
    }

    @GetMapping(path = "/annulla/{emailutente}:{idevento}")
    @ResponseBody
    public Map<String, Boolean> annullaEvento(@PathVariable("emailutente") String emailUtente,@PathVariable("idevento") int idEvento){
        return Collections.singletonMap("success", eventi.annullaEvento(jdbcTemplate,emailUtente,idEvento));
    }

    @GetMapping(value = "/getcategorie")
    @ResponseBody
    public List<Map<String,Object>> getCategorie(){
        return eventi.getCategorie(jdbcTemplate);
    }

    @RequestMapping(value = "/ispartecipante/{emailutente}:{idevento}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Boolean> isPartecipante(@PathVariable("emailutente") String emailUtente, @PathVariable("idevento") int idEvento){
        return Collections.singletonMap("success",eventi.isPartecipante(jdbcTemplate, emailUtente,idEvento));
    }

    @RequestMapping(value = "/isorganizzatore/{emailutente}:{idevento}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Boolean> isOrganizzatore(@PathVariable("emailutente") String emailUtente, @PathVariable("idevento") int idEvento){
        return Collections.singletonMap("success",eventi.isOrganizzatore(jdbcTemplate, emailUtente,idEvento));
    }
}
