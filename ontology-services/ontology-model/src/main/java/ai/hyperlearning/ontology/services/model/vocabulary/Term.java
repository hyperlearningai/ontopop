package ai.hyperlearning.ontology.services.model.vocabulary;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Simple Related Term Model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Entity
@Table(name = "terms")
public class Term implements Serializable {

	private static final long serialVersionUID = 194153329130178968L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private int nodeId;
	
	@NotNull
	private String userId;
	
	@Column(length = 100)
	private String term;
	
	@Basic
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateCreated;
	
	@Basic
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateLastUpdated;
	
	public Term() {
		
	}

	public Term(int id, int nodeId, @NotNull String userId, String term, 
			LocalDateTime dateCreated, LocalDateTime dateLastUpdated) {
		super();
		this.id = id;
		this.nodeId = nodeId;
		this.userId = userId;
		this.term = term;
		this.dateCreated = dateCreated;
		this.dateLastUpdated = dateLastUpdated;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public LocalDateTime getDateLastUpdated() {
		return dateLastUpdated;
	}

	public void setDateLastUpdated(LocalDateTime dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Term other = (Term) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Term ["
				+ "id=" + id + ", "
				+ "nodeId=" + nodeId + ", "
				+ "userId=" + userId + ", "
				+ "term=" + term + ", "
				+ "dateCreated=" + dateCreated + ", "
				+ "dateLastUpdated=" + dateLastUpdated + "]";
	}

}
