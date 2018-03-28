package com.imirisola.spring.logger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class RequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	
    	HttpServletRequest req = (HttpServletRequest) request;
    	String user = "";
    	String sessionID = "";
    	
    	if (req != null) {
    		if (req.getUserPrincipal() != null) {
    			user = req.getUserPrincipal().getName();
    		}
    		if (req.getSession(false) != null) {
    			sessionID = req.getSession().getId();
    		}
    	}
    	
        try {
            // Setup MDC data:
            String mdcData = String.format("[userId:%s | requestId:%s] ", user , sessionID);
            MDC.put("mdcData", mdcData); //Variable 'mdcData' is referenced in Spring Boot's logging.pattern.level property
            chain.doFilter(request, response);
        } finally {
           // Tear down MDC data:
           // ( Important! Cleans up the ThreadLocal data again )
           MDC.clear();
        }
    }

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}
}
