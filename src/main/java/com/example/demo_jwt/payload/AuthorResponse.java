package com.example.demo_jwt.payload;

import com.example.demo_jwt.enitity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class AuthorResponse {
    private String token;
    private  String username;
    private Collection<String> roles;

    public AuthorResponse(String token) {
        this.token = token;
    }

    public AuthorResponse(String token, String username, Collection<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

}
