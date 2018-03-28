package com.imirisola.spring.logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.imirisola.spring.security.ApplicationSecurity;


@SpringBootApplication
@ComponentScan(basePackages = {"com.imirisola.spring.security", "com.imirisola.spring.logger"} )
@EnableAutoConfiguration
public class LoggerApp {
	
    @Bean
    public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter() {
        return new ApplicationSecurity();
    }
    
	public static void main(String[] args) {
		SpringApplication.run(LoggerApp.class, args);
	}

}
