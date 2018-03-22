package com.imirisola.spring.logger;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("hello")
@Slf4j
public class Hello {

	public Hello() {
		super();
		MDC.put("sessionId", "i807056");
	}

	@GetMapping
    public String getAllDeals() {
    	log.debug("DEBUG: Hello me... meet the real me! ");
    	log.warn("WARN: Hello me... meet the real me! ");
    	log.info("INFO: Hello me... meet the real me! ");
    	log.error("ERROR: Hello me... meet the real me! ");
    	log.trace("TRACE: Hello me... meet the real me! ");
    	
        return "Hello";
    }
}
