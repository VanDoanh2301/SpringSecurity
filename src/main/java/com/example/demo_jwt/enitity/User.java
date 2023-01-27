package com.example.demo_jwt.enitity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;

import java.util.Collection;

@Entity
@Table(name = "users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    @Column(name="user_Name", length = 120, nullable = false,unique = true)
    private  String userName;
    @Column(name="pass_Word", length = 120, nullable = false)
    private  String passWord;
    @Column(name="Email", length = 120, nullable = false,unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
    joinColumns = @JoinColumn(name = "user_Id",nullable = true),
            inverseJoinColumns = @JoinColumn(name = "role_Id")
    )
    private Collection<Role> roles;
    public User(int id, String userName, String passWord, String email, Collection<Role> roles) {
        this.id = id;
        this.userName = userName;
        this.passWord = passWord;
        this.email = email;
        this.roles = roles;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
}
