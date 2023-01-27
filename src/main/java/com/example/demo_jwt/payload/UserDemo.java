package com.example.demo_jwt.payload;

import com.example.demo_jwt.enitity.Privilege;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDemo {
    private  String name;
    private  String privilege;

    private Collection<String> privileges;

    private  int id;
}
