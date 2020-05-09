package com.rv.justmeet.controller;

import com.rv.justmeet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Classe controller di un utente
 *
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 */
@RestController
@RequestMapping(path="/utente")
public class UserController {
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository utenti;
    

    @RequestMapping(path="/registrazione", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Map<String, Boolean> registrazione(@RequestBody UserRepository user){
        return Collections.singletonMap("success",user.registrazione(jdbcTemplate));
    }

    @RequestMapping(path="/login", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Map<String, Boolean> login(@RequestBody UserRepository user){
        return Collections.singletonMap("success",user.login(jdbcTemplate));
    }

    @RequestMapping(value = "/partecipa/{emailutente}:{idevento}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> partecipa(@PathVariable("emailutente") String emailUtente, @PathVariable("idevento") int idEvento){
        return Collections.singletonMap("success",utenti.partecipa(jdbcTemplate, emailUtente,idEvento));
    }

    @RequestMapping(value = "/controlloemail/{emailutente}",method = RequestMethod.GET )
    @ResponseBody
    public Map<String, Boolean> controlloEmail(@PathVariable("emailutente") String emailUtente){
        return Collections.singletonMap("success",utenti.controlloEmail(jdbcTemplate,emailUtente));
    }

    @RequestMapping(value = "/annullapartecipazione/{emailutente}:{idevento}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Boolean> annullaPartecipazione(@PathVariable("idevento") int idEvento,@PathVariable("emailutente") String emailUtente){
        return Collections.singletonMap("success",utenti.annullaPartecipazione(jdbcTemplate,idEvento,emailUtente));
    }

    @RequestMapping(value = "/eliminatest")
    @ResponseBody
    public Map<String, Boolean> eliminatest(){
        return Collections.singletonMap("success",utenti.eliminaTest(jdbcTemplate));
    }

    @RequestMapping(value = "/eliminatest2")
    @ResponseBody
    public Map<String, Boolean> eliminatest2(){
        return Collections.singletonMap("success",utenti.eliminaTest2(jdbcTemplate));
    }

    @GetMapping(value = "/getprofilo/{emailutente}")
    @ResponseBody
    public List<Map<String,Object>> getProfilo(@PathVariable("emailutente") String emailUtente){
        return utenti.getProfilo(emailUtente,jdbcTemplate);
    }

    @GetMapping(value = "/modifica/{emailutente}:{nomeCampo}:{valore}")
    @ResponseBody
    public Map<String,Boolean> getProfilo(@PathVariable("emailutente") String emailUtente, @PathVariable("nomeCampo") String nomeCampo, @PathVariable("valore") String valoreDaModificare){
        return Collections.singletonMap("success",utenti.modifica(emailUtente,nomeCampo,valoreDaModificare,jdbcTemplate));
    }

}
