package ai.hyperlearning.ontology.services.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Single User Model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class User implements UserDetails {

	private static final long serialVersionUID = -8941894457664074525L;
	private String username;
	private String password;
	
	public User(final String username, final String password) {
		super();
	    this.username = requireNonNull(username);
	    this.password = password;
	}
	
	@JsonIgnore
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return new ArrayList<>();
	}
	
	@Override
	public String getUsername() {
		return username;
	}
	
	@JsonIgnore
	@Override
	public String getPassword() {
		return password;
	}
	
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

}
