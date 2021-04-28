package ai.hyperlearning.ontology.services.model.collaboration;

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
 * Simple Note Model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Entity
@Table(name = "notes")
public class Note implements Serializable {

	private static final long serialVersionUID = 4830210791998223754L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@NotNull
	private String type;
	
	private int nodeId;
	private int edgeId;
	
	@NotNull
	private String userId;
	
	@Column(length = 1000)
	private String contents;
	
	@Basic
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateCreated;
	
	@Basic
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateLastUpdated;
	
	public Note() {
		
	}

	public Note(int id, String type, int nodeId, int edgeId, 
			String userId, String contents, LocalDateTime dateCreated,
			LocalDateTime dateLastUpdated) {
		super();
		this.id = id;
		this.type = type;
		this.nodeId = nodeId;
		this.edgeId = edgeId;
		this.userId = userId;
		this.contents = contents;
		this.dateCreated = dateCreated;
		this.dateLastUpdated = dateLastUpdated;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getEdgeId() {
		return edgeId;
	}

	public void setEdgeId(int edgeId) {
		this.edgeId = edgeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
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
		Note other = (Note) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Note ["
				+ "id=" + id + ", "
				+ "type=" + type + ", "
				+ "nodeId=" + nodeId + ", "
				+ "edgeId=" + edgeId + ", "
				+ "userId=" + userId + ", "
				+ "contents=" + contents + ", "
				+ "dateCreated=" + dateCreated + ", "
				+ "dateLastUpdated=" + dateLastUpdated + "]";
	}

}
