package com.example.demo_jwt.enitity;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name="name", length = 120, nullable = false)
    private String name;
    public Privilege() {
    }

    public Privilege(String name) {
        this.name = name;
    }

    public Privilege(Long id, String name, Collection<Role> roles) {
        this.id = id;
        this.name = name;
//        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Collection<Role> getRoles() {
//        return roles;
//    }
//
//    public void setRoles(Collection<Role> roles) {
//        this.roles = roles;
//    }
}