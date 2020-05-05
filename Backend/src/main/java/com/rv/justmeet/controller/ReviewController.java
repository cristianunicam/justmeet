package com.rv.justmeet.controller;

import com.rv.justmeet.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Classe controller delle recensioni
 *
 * @author Cristian Verdecchia, Lorenzo Romagnoli
 */
@RestController
@RequestMapping(path="/recensioni")
public class ReviewController {
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    public ReviewRepository review;

    @RequestMapping(path="/inserimento", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Map<String, Boolean> pubblicaRecensioni(@RequestBody ReviewRepository recensione){
        return Collections.singletonMap("success",recensione.inserimento(jdbcTemplate));
    }

    @GetMapping(value = "/visualizzarecensioni/{emailutente}")
    @ResponseBody
    public List<Map<String,Object>> getRecensioni(@PathVariable("emailutente") String emailUtente){
        return review.getRecensioni(emailUtente,jdbcTemplate);
    }
}
