package dbmapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;




@Entity
@Table(name="account")
public class Account implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Account(){
		
	}
	
	public Account(String login, String password){
		this.login = login;
		this.password = password;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}



	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String login;
	
	@JsonIgnore
	private String password;
	
	@OneToMany(mappedBy="account",fetch=FetchType.EAGER)
	@JsonBackReference
	private List<User> userList = new ArrayList<User>();
	
}
