package com.example.demo_jwt.Permission;

import com.example.demo_jwt.enitity.Privilege;
import com.example.demo_jwt.enitity.Role;
import com.example.demo_jwt.enitity.User;
import com.example.demo_jwt.repostory.PrivilegeRepostory;
import com.example.demo_jwt.repostory.RoleRepostory;
import com.example.demo_jwt.repostory.UserRepostory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    boolean alreadySetup =true;
    @Autowired
    private RoleRepostory roleRepo;
    @Autowired
    private PrivilegeRepostory privilegeRepo;
    @Autowired
    private  UserRepostory userRepo;
    private PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Privilege admin
                = createPrivilegeIfNotFound("ADMIN_ALL");
        Privilege readUser
                = createPrivilegeIfNotFound("USER_READ");
        Privilege signin
                = createPrivilegeIfNotFound("USER_SIGNIN");
        Privilege signup
                = createPrivilegeIfNotFound("USER_SIGNUP");
        Privilege remove
                = createPrivilegeIfNotFound("REMOVE_USER");
//        User user =User.builder()
//                .userName("Admin")
//                .passWord(encoder().encode("123"))
//                .email("admin@gmail.com")
//                .roles(Arrays.asList(new Role("ADMIN")))
//                .build();
//        userRepo.save(user);
//
//        Collection<Role> roles = user.getRoles();
//        Collection<Privilege> privileges = Arrays.asList(admin,readUser,signin,signup,remove);
//        roles.forEach(role -> {
//            role.setPrivileges(privileges);
//            roleRepo.save(role);
//        });

//        List<Privilege> adminPrivileges = Arrays.asList(
//               removeUser, readUser,signin);
//        createRoleIfNotFound("ADMIN",adminPrivileges);
//        createRoleIfNotFound("USER",Arrays.asList(readUser));
        alreadySetup = true;
    }
    @Transactional
    //Khoi tao privilege neu ko tim thay tien hanh tao
    Privilege createPrivilegeIfNotFound(String name) {
         Privilege privilege = privilegeRepo.findByName(name);
         if(privilege == null) {
            privilege = new Privilege(name);
            privilegeRepo.save(privilege);
         }
         return privilege;
    }
    @Transactional
        //Khoi tao Role neu ko tim thay tien hanh tao
    Role createRoleIfNotFound(String name,Collection<Privilege> privilege) {
        Role role = roleRepo.findByName(name);
        if(role == null) {
            role = new Role(name);
            role.setPrivileges(privilege);
           roleRepo.save(role);
        }
        return role;
    }

}
