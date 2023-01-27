package com.example.demo_jwt;

import com.example.demo_jwt.Dto.UserDto;
import com.example.demo_jwt.config.jwt.JwtProvider;
import com.example.demo_jwt.enitity.Privilege;
import com.example.demo_jwt.enitity.Role;
import com.example.demo_jwt.enitity.User;
import com.example.demo_jwt.repostory.PrivilegeRepostory;
import com.example.demo_jwt.repostory.RoleRepostory;
import com.example.demo_jwt.repostory.UserRepostory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserSaveTest {

    @Autowired
    private UserRepostory userRepo;
    @Autowired
    private RoleRepostory roleRepo;

    @Autowired
    private PrivilegeRepostory privilegeRepo;
   public PasswordEncoder encoder(){
       return new BCryptPasswordEncoder();
    }


    @Test
    public void saveUserTest() {

        User user =User.builder()
                .userName("mon")
                .passWord(encoder().encode("123"))
                .email("son@gmail.com")
                .roles(Arrays.asList(new Role("USER")))
                .build();
        userRepo.save(user);
    }
    @Test
    public void deleteUser() {
        userRepo.deleteById(2);
    }
    @Test
    public void getAllUser() {
        List<User> users = userRepo.findAll();
        System.out.println(users);
    }

    @Test
    public void updateUser() {
       List<Role> roles = roleRepo.findId(3);
       Privilege p = privilegeRepo.findByName("USER_READ");
       roles.forEach(role -> {
           Collection<Privilege> privileges = role.getPrivileges();
           privileges.forEach(privilege -> {
               if(privilege.getName()== p.getName()){
                   privileges.remove(privilege);
               }

           });  roleRepo.save(role);
       });

    }
    @Test
public  void nativeQuery() {

       roleRepo.deleteRoleId(4,"USER_SIGNIN");

    }
}
