package dbmapping;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

@Entity
@Table(name="message")
public class Message implements Serializable{


	public Long getDialog_id() {
		return dialog_id;
	}
	public void setDialog_id(Long dialog_id) {
		this.dialog_id = dialog_id;
	}
	public Long getOwnerID() {
		return ownerID;
	}
	public void setOwnerID(Long ownerID) {
		this.ownerID = ownerID;
	}
	public boolean isMsgOwner() {
		return msgOwner;
	}
	public void setMsgOwner(boolean msgOwner) {
		this.msgOwner = msgOwner;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	@JsonIgnore
	public void setId(long id) {
		this.id = id;
	}
	
	public void setSend_time(Date send_time) {
		this.send_time = send_time;
	}


	public long getId() {
		return id;
	}


	public String getMessage() {
		return message;
	}

	public Date getSend_time() {
		return send_time;
	}

	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@JsonIgnore
	@Column(name="dialog_id")
	private Long dialog_id;
	
	@Column(name="msg_owner",columnDefinition ="TINYINT(1)")
	@JsonIgnore
	private boolean msgOwner;
	
	private String message;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd hh:mm:ss")
	private Date send_time;


	private boolean readed;

	@Transient
	@JsonProperty("sender")
	private Long ownerID;
	
}
