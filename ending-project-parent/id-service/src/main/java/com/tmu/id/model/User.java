package com.tmu.id.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name="user")

public class User implements Serializable {

    private static final long serialVersionUid = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "created_at" )
    private Timestamp createdAt;

    public User() {
    }

    public User(String username, String password, String fullName, String roleName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.roleName = roleName;
    }

    @PrePersist
    void preInsert() {
        if (this.getCreatedAt() == null)
            this.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @PreUpdate
    void preUpdate() {
        if (this.getCreatedAt() == null)
            this.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName(){ return username; }

    public void setUserName(String username){ this.username = username; }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName(){ return fullName; }

    public void setFullName(String fullName){ this.fullName = fullName; }

    public String getRoleName(){ return roleName; }

    public void setRoleName(String roleName){ this.roleName = roleName; }

    public Timestamp getCreatedAt(){ return createdAt; }

    public void setCreatedAt(Timestamp createdAt){ this.createdAt =createdAt; }


}
