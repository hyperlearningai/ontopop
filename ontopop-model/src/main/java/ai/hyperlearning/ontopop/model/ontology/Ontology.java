package ai.hyperlearning.ontopop.model.ontology;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Ontology Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class Ontology implements Serializable {

	private static final long serialVersionUID = 897119992423827281L;
	
	@Id
	@GeneratedValue
	private String uuid;
	
	@NotNull
	private String inputUri;
	
	@Basic
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@GeneratedValue
	private LocalDateTime dateCreated;
	
	public Ontology() {
		this.uuid = UUID.randomUUID().toString();
		this.dateCreated = LocalDateTime.now();
	}

	public Ontology(@NotNull String inputUri) {
		this.uuid = UUID.randomUUID().toString();
		this.dateCreated = LocalDateTime.now();
		this.inputUri = inputUri;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getInputUri() {
		return inputUri;
	}

	public void setInputUri(String inputUri) {
		this.inputUri = inputUri;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		Ontology other = (Ontology) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Ontology ["
				+ "uuid=" + uuid + ", "
				+ "inputUri=" + inputUri + ", "
				+ "dateCreated=" + dateCreated + "]";
	}	
	
}
