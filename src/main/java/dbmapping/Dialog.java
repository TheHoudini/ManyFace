package dbmapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="dialog")
public class Dialog {

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Dialog(){
		
	}
	
	public Dialog(long firstUserId , long secondUserId){
		this.fUser = firstUserId;
		this.sUser = secondUserId;
	}
	public User getFirstUser() {
		return firstUser;
	}

	public void setFirstUser(User firstUser) {
		this.firstUser = firstUser;
	}

	public User getSecondUser() {
		return secondUser;
	}

	public void setSecondUser(User secondUser) {
		this.secondUser = secondUser;
	}



	public long getfUser() {
		return fUser;
	}

	public void setfUser(long fUser) {
		this.fUser = fUser;
	}

	public long getsUser() {
		return sUser;
	}

	public void setsUser(long sUser) {
		this.sUser = sUser;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="fUser")
	private long fUser;
	
	@Column(name="sUser")
	private long sUser;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="fUser", insertable = false, updatable = false)
	@JsonIgnore
	private User firstUser;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="sUser", insertable = false, updatable = false)
	@JsonIgnore
	private User secondUser;
}
