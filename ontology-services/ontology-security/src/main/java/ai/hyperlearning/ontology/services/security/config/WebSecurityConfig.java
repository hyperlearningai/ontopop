package ai.hyperlearning.ontology.services.security.config;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ai.hyperlearning.ontology.services.security.auth.CustomAuthenticationFailureHandler;
import ai.hyperlearning.ontology.services.security.auth.FileAuthenticationProvider;
import ai.hyperlearning.ontology.services.security.auth.OpenLDAPAuthenticationProvider;
import ai.hyperlearning.ontology.services.security.auth.TokenAuthenticationFilter;
import ai.hyperlearning.ontology.services.utils.GlobalProperties;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Web Security Configuration
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	// Authentication providers
	private static final String AUTH_PROVIDER_FILE = "file";
	private static final String AUTH_PROVIDER_OPENLDAP = "openldap";
	private static final String AUTH_PROVIDER_AZURE_ACTIVE_DIRECTORY = 
			"azure-active-directory";
	
	// Define a list of URLs that are publicly available
	private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
			new AntPathRequestMatcher("/api/**", 
					HttpMethod.GET.toString()), 
			new AntPathRequestMatcher("/api/auth/login", 
					HttpMethod.POST.toString()),
			new AntPathRequestMatcher("/api/auth/token/refresh", 
					HttpMethod.POST.toString())
	);
	
	// All other URLs will be secured requiring authorisation
	private static final RequestMatcher PROTECTED_URLS = 
			new NegatedRequestMatcher(PUBLIC_URLS);
	
	@Autowired
	private GlobalProperties globalProperties;
	
	@Autowired
    private FileAuthenticationProvider fileAuthenticationProvider;
	
	@Autowired
	private OpenLDAPAuthenticationProvider openLDAPAuthenticationProvider;
	
	/**
	 * Get the authentication provider
	 * @return
	 */
	
	private AuthenticationProvider getAuthenticationProvider() {
		
		AuthenticationProvider authenticationProvider = null;
		switch(globalProperties.getOntologyAuthProvider()) {
			
			// Simple users.properties file
			case AUTH_PROVIDER_FILE:
				authenticationProvider = fileAuthenticationProvider;
				break;
				
			// OpenLDAP server
			case AUTH_PROVIDER_OPENLDAP:
				authenticationProvider = openLDAPAuthenticationProvider;
				break;
			
			// Azure Active Directory
			case AUTH_PROVIDER_AZURE_ACTIVE_DIRECTORY:
				break;
			
			// Default - OpenLDAP server
			default:
				authenticationProvider = openLDAPAuthenticationProvider;
				
		}
		
		return authenticationProvider;
		
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(getAuthenticationProvider());
	}
	
	@Override
	public void configure(final WebSecurity web) {
		web.ignoring().requestMatchers(PUBLIC_URLS);
	}
	
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
			.cors(withDefaults())
			.sessionManagement()
			.sessionCreationPolicy(STATELESS)
			.and()
			.exceptionHandling()
			.defaultAuthenticationEntryPointFor(
					forbiddenEntryPoint(), PROTECTED_URLS)
			.and()
			.authenticationProvider(getAuthenticationProvider())
			.addFilterBefore(restAuthenticationFilter(), 
					AnonymousAuthenticationFilter.class)
			.authorizeRequests()
			.requestMatchers(PROTECTED_URLS)
			.authenticated()
			.and()
			.csrf().disable()
			.formLogin().disable()
			.httpBasic().disable()
			.logout().disable();
	}
	
	@Bean
	TokenAuthenticationFilter restAuthenticationFilter() throws Exception {
		final TokenAuthenticationFilter filter = 
				new TokenAuthenticationFilter(PROTECTED_URLS);
	    filter.setAuthenticationManager(authenticationManager());
	    filter.setAuthenticationSuccessHandler(successHandler());
	    filter.setAuthenticationFailureHandler(authenticationFailureHandler());
	    return filter;
	}
	
	@Bean
	SimpleUrlAuthenticationSuccessHandler successHandler() {
		final SimpleUrlAuthenticationSuccessHandler successHandler = 
				new SimpleUrlAuthenticationSuccessHandler();
	    successHandler.setRedirectStrategy(new NoRedirectStrategy());
	    return successHandler;
	}
	
	@Bean
	FilterRegistrationBean<TokenAuthenticationFilter> disableAutoRegistration(
			final TokenAuthenticationFilter filter) {
		final FilterRegistrationBean<TokenAuthenticationFilter> registration = 
				new FilterRegistrationBean<TokenAuthenticationFilter>(filter);
	    registration.setEnabled(false);
	    return registration;
	}
	
	@Bean
	AuthenticationEntryPoint forbiddenEntryPoint() {
		return new HttpStatusEntryPoint(FORBIDDEN);
	}
	
	@Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	
	@Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
	
	@Bean
	public UserDetailsService userDetailsService() {
		try {
			Properties users = PropertiesLoaderUtils
					   .loadAllProperties("ontology-users.properties");
			return new InMemoryUserDetailsManager(users);
		} catch (IOException e) {
			
		}
		return null;
	}
	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = 
        		new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
