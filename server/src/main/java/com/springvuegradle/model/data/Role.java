package com.springvuegradle.model.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * JPA POJO representing a Role.
 */
@Entity
@Table(name = "role")
public class Role {

    @Id
    @NotNull
    private long role_id;

    // the @NotNull annotation will automatically set the column to not null
    // if hibernate.validator.apply_to_ddl = true (true by default)

    @NotNull
    @Column(columnDefinition = "varchar(30)")
    private String rolename;


    /**
     * no arg constructor required by JPA
     */
    public Role() {}

    /**
     * Create a role with required id and name fields
     * @param id the id of the role
     * @param rolename the name of the role
     */
    public Role(long id, String rolename)
    {
        this.role_id = id;
        this.rolename = rolename;
    }

	public long getRole_id() {
		return role_id;
	}

	public void setRole_id(long role_id) {
		this.role_id = role_id;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

}
