package com.imirisola.spring.logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("hello")
@Slf4j
public class Hello {
	
    @GetMapping
    public String getAllDeals() {
    	log.trace("Hello me... meet the real me! ");
        return "Hello";
    }
}
