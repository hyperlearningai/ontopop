package ai.hyperlearning.ontology.services.model.ui;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple UI Styling Model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Entity
@Table(name = "styling")
public class Styling implements Serializable {
	
	private static final long serialVersionUID = 9013437692739183140L;
	
	@Id
	private String userId;
	
	@Column(length = 5000)
	@JsonIgnore
	private String stringConfiguration;
	
	@JsonInclude()
	@Transient
	private JsonNode configuration;
	
	public Styling() {
		
	}

	public Styling(String userId, JsonNode configuration) {
		super();
		this.userId = userId;
		this.configuration = configuration;
		this.stringConfiguration = ( configuration != null ) ? 
				configuration.toString() : null;
			
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStringConfiguration() {
		return stringConfiguration;
	}

	public void setStringConfiguration(String stringConfiguration) {
		this.stringConfiguration = stringConfiguration;
		try {
			this.configuration = ( stringConfiguration != null ) ? 
					new ObjectMapper().readTree(stringConfiguration) : null;
		} catch (Exception e) {
			
		}
	}

	public JsonNode getConfiguration() {
		return configuration;
	}

	public void setConfiguration(JsonNode configuration) {
		this.configuration = configuration;
		this.stringConfiguration = ( configuration != null ) ? 
				configuration.toString() : null;
	}
	
	public void setConfiguration(String stringConfiguration) {
		try {
			this.configuration = ( stringConfiguration != null ) ? 
					new ObjectMapper().readTree(stringConfiguration) : null;
		} catch (Exception e) {
			
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Styling other = (Styling) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Styling ["
				+ "userId=" + userId + ", "
				+ "configuration=" + stringConfiguration + "]";
	}

}
