package com.example.demo_jwt.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorRequest {

    private String username;
    private  String password;

}
