package com.neo.adminserver.configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import de.codecentric.boot.admin.server.config.AdminServerProperties;

public class SecurityConfig extends WebSecurityConfiguration {

	private final AdminServerProperties adminServer;

	public SecurityConfig(AdminServerProperties adminServer) {
		this.adminServer = adminServer;
	}

	protected void configure(HttpSecurity http) throws Exception {

		SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
		successHandler.setTargetUrlParameter("redirectTo");
		successHandler.setDefaultTargetUrl(this.adminServer.path("/"));

		http.authorizeRequests().requestMatchers(this.adminServer.path("/assets/**")).permitAll()
				.requestMatchers(this.adminServer.path("/login")).permitAll().anyRequest().authenticated().and()
				.formLogin().loginPage(this.adminServer.path("/login")).successHandler(successHandler).and().logout()
				.logoutUrl(this.adminServer.path("/logout")).and().httpBasic().and().csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringRequestMatchers(
																										
						this.adminServer.path("/instances"), this.adminServer.path("/actuator/**"));

	}

}
