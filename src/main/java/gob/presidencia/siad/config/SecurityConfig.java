package gob.presidencia.siad.config;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Bean
	AccessDeniedHandler customAccessDeniedHandler() {
	    return (request, response, accessDeniedException) -> {
	    	logger.info("acceso denegado del request: " + request.getRequestURI());
            if(request.getContentType()!=null && request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) response.sendError(HttpServletResponse.SC_FORBIDDEN);
            else response.sendRedirect("/siad/accessDenied");
	    };
	}
	
	@Bean
	AuthenticationEntryPoint customAuthenticationEntryPoint() {
	    return (request, response, authException) -> {
	    	logger.info("usuario no autenticado, request: " + request.getRequestURI());
            response.sendRedirect("/siad/login");
	    };
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http.csrf(AbstractHttpConfigurer::disable)
	      .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
	    	  authorizationManagerRequestMatcherRegistry
		    	  .requestMatchers("/resources/**").permitAll()
		    	  .requestMatchers("/static/**").permitAll()
		    	  .requestMatchers("/css/**").permitAll()
		    	  .requestMatchers("/js/**").permitAll()
		    	  .requestMatchers("/images/**").permitAll()
		    	  .requestMatchers("/webfonts/**").permitAll()
		    	  .requestMatchers("/login").permitAll()
		    	  .requestMatchers("/login").permitAll()
		    	  .requestMatchers("/loginadmform").permitAll()
		    	  .requestMatchers("/error").permitAll()
		    	  .anyRequest().authenticated();
	      })
	      .logout((logout) -> logout.logoutUrl("/siad/logout"))
	      .httpBasic(Customizer.withDefaults())
	      .exceptionHandling(customizer -> {
	    	  customizer.accessDeniedHandler(customAccessDeniedHandler());
	    	  customizer.authenticationEntryPoint(customAuthenticationEntryPoint());
	      });

	    return http.build();
	}
}
