package com.imirisola.spring.logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class Hello {
	
    @GetMapping
    public String getAllDeals() {
    	log.info("Hello me... meet the real me! ");
    	log.error("Hello me... meet the real me! ");
    	log.warn("Hello me... meet the real me! ");
    	log.debug("Hello me... meet the real me! ");
    	log.trace("Hello me... meet the real me! ");
        return "Hello";
    }
}
