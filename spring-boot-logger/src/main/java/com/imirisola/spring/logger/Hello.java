package com.imirisola.spring.logger;

import java.io.File;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class Hello {
	
	private static String POST_MSG = "Post method via helloPost() was called. ";
	private static String GET_MSG = "Get method via getHello() was called. ";
	private static LogFileRecorderWriter writer = new LogFileRecorderWriter(new File("/usr/sap/ljs/log"));
	
    @RequestMapping("/")
    public String home() {
    	return "home";
    }
	
	
	@GetMapping(value = "/hello")
    public String getHello(HttpServletRequest request, Principal principal) {
    	log.info(GET_MSG);
    	log.error(GET_MSG);
    	log.warn(GET_MSG);
    	log.debug(GET_MSG);
    	log.trace(GET_MSG);

    	long timestamp = System.currentTimeMillis();
    	
    	String user = "";
    	if (principal != null) {
    		user = principal.getName();
    	}

    	writer.write(timestamp, user , request, GET_MSG, this);
    	
        return "Hello";
    }
	
	@PostMapping(value = "/hello")
	@ResponseBody
	public String helloPost(HttpServletRequest request, Principal principal) {

    	log.info(POST_MSG);
    	log.error(POST_MSG);
    	log.warn(POST_MSG);
    	log.debug(POST_MSG);
    	log.trace(POST_MSG);		

		long timestamp = System.currentTimeMillis();
    	
    	String user = "";
    	if (principal != null) {
    		user = principal.getName();
    	}
    	
		writer.write(timestamp, user , request, POST_MSG, this);
		
		return "Happy posting, " + principal.getName() + "!";
		
		
	}


}
