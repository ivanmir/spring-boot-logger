package com.imirisola.spring.logger;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoggerApp {
	
    static {
    	SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
    
	public static void main(String[] args) {
		SpringApplication.run(LoggerApp.class, args);
	}

}
