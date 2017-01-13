package dbmapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="user")
public class User implements Serializable {
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(long account_id) {
		this.account_id = account_id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhotoLink(){
		return "http://151.248.123.236/api/v1/user/" + String.valueOf(id) +"/photo";
	}



	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@JsonIgnore
	private long account_id;
	
	private String username;
	
	private String description;

	@JsonIgnore
	public byte[] getPhoto() {
		return photo;
	}
	@JsonIgnore
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	@JsonIgnore
	@Lob
	@Column(name="photo")
	@Basic(fetch = FetchType.LAZY)

	private byte[] photo;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="account_id", insertable = false, updatable = false)
	@JsonIgnore
	private Account account;



}
