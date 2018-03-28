package com.imirisola.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
 
    @Autowired
    private SecurityProperties securityProperties;	

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.inMemoryAuthentication().withUser("user").password("user").roles("USER").and().withUser("admin")
                .password("admin").roles("ADMIN");
    }
 
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        .anyRequest().fullyAuthenticated()
        .and().formLogin().loginPage("/login").failureUrl("/login?error").permitAll()
        .and().logout().permitAll();

		// CSRF tokens handling
        if (securityProperties.isEnableCsrf()) {
            http.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
        } else {
            http.csrf().disable();
        }
	}
}
