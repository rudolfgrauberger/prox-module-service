package io.archilab.projektboerse.moduleservice.config;

import java.util.Arrays;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebSecurity
class KeyCon extends KeycloakWebSecurityConfigurerAdapter
{
	/**
	 * Registers the KeycloakAuthenticationProvider with the authentication manager.
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		auth.authenticationProvider(keycloakAuthenticationProvider);
	}

	/**
	 * Defines the session authentication strategy.
	 */
	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
//		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
		return new NullAuthenticatedSessionStrategy();
	}

	@Bean
	public KeycloakConfigResolver KeycloakConfigResolver() {return new KeycloakSpringBootConfigResolver();}

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		super.configure(http);
		http
		 .csrf().disable()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // STATELESS
      .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
      .and()
      .authorizeRequests()
      .antMatchers(HttpMethod.GET,"/module*").permitAll()
 	    .antMatchers(HttpMethod.GET,"/module/*").permitAll()
 	    .antMatchers(HttpMethod.GET,"/module/**").permitAll()
 	   .antMatchers("/module*").denyAll()
	    .antMatchers("/module/*").denyAll()
	    .antMatchers("/module/**").denyAll()
 	   .antMatchers(HttpMethod.GET,"/studyCourse*").permitAll()
	    .antMatchers(HttpMethod.GET,"/studyCourse/*").permitAll()
	    .antMatchers(HttpMethod.GET,"/studyCourse/**").permitAll()
		   .antMatchers("/studyCourse*").denyAll()
		    .antMatchers("/studyCourse/*").denyAll()
		    .antMatchers("/studyCourse/**").denyAll()
		    .antMatchers("/").permitAll()
		    .antMatchers("/profile*").permitAll()
		    .antMatchers("/profile/*").permitAll()
		    .antMatchers("/profile/**").permitAll()
		    
//      .antMatchers("/*").permitAll()
      .anyRequest().denyAll();
//	    http
//	    .cors()
//        .and()
//	    .csrf()
//        .disable()     
//	    
//        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // STATELESS
//        .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
//        .and()
//        .authorizeRequests()
//	    .antMatchers("/people/*").hasRole("user")
//		.antMatchers("/people*").hasRole("user")
//		.antMatchers("/qqq*").hasRole("user")
//		.antMatchers(HttpMethod.POST, "/module/*").hasAnyRole("manage-all", "user")
//        .antMatchers(HttpMethod.PUT, "/module/*").hasRole("user")
//        
//		.antMatchers("/**").authenticated()
//        .anyRequest().denyAll();
////		.antMatchers("/*").anonymous()
////		.anyRequest().permitAll();
	}
	
	

	@Bean
	public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(
	        KeycloakAuthenticationProcessingFilter filter) {
	    FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
	    registrationBean.setEnabled(false);
	    return registrationBean;
	}

	@Bean
	public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(
	        KeycloakPreAuthActionsFilter filter) {
	    FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
	    registrationBean.setEnabled(false);
	    return registrationBean;
	}
	
}
	
	

