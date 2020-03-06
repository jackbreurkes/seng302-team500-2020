package com.springvuegradle.model.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin implements Serializable {

	/**
	 * Generated serializable id
	 */
	private static final long serialVersionUID = -2278649311090516826L;
	
	@Id
	private long id;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "uuid")
	private User user;
	
	protected Admin() {}
	
	public Admin(User user) {
		this.user = user;
		this.id = user.getUserId();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
