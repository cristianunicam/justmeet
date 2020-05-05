package com.rv.justmeet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe per inizializzare il backend con l'utilizzo del framework spring boot
 *
 * @author Cristian Verdecchia Lorenzo Romagnoli
 */
@SpringBootApplication
public class JustmeetApplication {
	private static final Logger log = LoggerFactory.getLogger(JustmeetApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JustmeetApplication.class, args);
	}

}
