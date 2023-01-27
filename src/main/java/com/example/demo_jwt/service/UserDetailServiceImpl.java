package com.example.demo_jwt.service;

import com.example.demo_jwt.repostory.UserRepostory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
@Autowired
private UserRepostory repo;
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        com.example.demo_jwt.enitity.User user = repo.getUserByName(userName);
        if(user == null) {
            throw  new UsernameNotFoundException("User not found");
        }

        //Logic to get the user form the Database
        return new UserDetailImpl(user);
    }
}
