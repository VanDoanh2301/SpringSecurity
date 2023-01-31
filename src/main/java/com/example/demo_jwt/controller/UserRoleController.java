package com.example.demo_jwt.controller;

import com.example.demo_jwt.Dto.UserDto;
import com.example.demo_jwt.enitity.Privilege;
import com.example.demo_jwt.enitity.Role;
import com.example.demo_jwt.enitity.User;
import com.example.demo_jwt.payload.UserDemo;
import com.example.demo_jwt.repostory.PrivilegeRepostory;
import com.example.demo_jwt.repostory.RoleRepostory;
import com.example.demo_jwt.repostory.UserRepostory;
import com.example.demo_jwt.service.UserDetailImpl;
import com.example.demo_jwt.service.serviceimpl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class UserRoleController {
    @Autowired
    private UserRepostory userRepo;
    @Autowired
    private PrivilegeRepostory privilegeRepo;
    @Autowired
    private RoleRepostory roleRepo;
    @Autowired
    private UserService userService;

    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    @GetMapping("/all")
    public String allRole() {
        return "all success";
    }
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('USER_READ')")
    public String userRole() {
        return "USER success";
    }
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN_ALL')")
    public String adminRole() {
        return "ADMIN success";
    }

    @GetMapping("/signin")
    @PreAuthorize("hasAuthority('USER_SIGNIN')")
    public String singin() {
        return "Singin success";
    }

    @GetMapping("/signup")
    @PreAuthorize("hasAuthority('USER_SIGNUP')")
    public String signup() {
        return "Signup success";
    }
    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<?> demo(@RequestBody UserDemo userDemo) {
        User user = userRepo.getUserByName(userDemo.getName());
        Collection<Role> roles = user.getRoles();
        Privilege privilege = privilegeRepo.findByName(userDemo.getPrivilege());
        roles.forEach(role -> {
            Collection<Privilege> privileges =  role.getPrivileges();
            privileges.add(privilege);
            roleRepo.save(role);
        });
        return  ResponseEntity.ok(user);
    }
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<?> add(@RequestBody UserDto userDto) {
        User user  = userService.saveUser(userDto);
        Collection<Role> roles = user.getRoles();
        return  ResponseEntity.ok(user);
    }
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto) {
        Privilege privilege = privilegeRepo.findByName("USER_READ");
        Collection<String> roleDto = userDto.getRole();
        Collection<Role>  listRole = new ArrayList<>();
        roleDto.forEach(ro -> {
                            Role userRole = roleRepo.findByName(ro);
                            if (userRole == null) {
                                userRole = new Role(ro);
                                roleRepo.save(userRole);
                            }
                            listRole.add(userRole);
                });
//                userRepo.findById(userDto.getId()) .ifPresent(user1 -> {
//                    user1.setUserName(userDto.getUsername());
//                    user1.setPassWord(encoder().encode(userDto.getPassword()));
//                    user1.setEmail(userDto.getEmail());
//                    user1.setRoles(listRole);
//                    userRepo.save(user1);
//                });
        User user = userRepo.getUserByName(userDto.getUsername());
        if(user != null) {
            user.setUserName(userDto.getUsername());
                    user.setPassWord(encoder().encode(userDto.getPassword()));
                    user.setEmail(userDto.getEmail());
                    user.setRoles(listRole);
                    userRepo.save(user);
        }
//        Collection<Role> roles = user.getRoles();
//        Collection<Privilege> privileges = new ArrayList<>();
//        privileges.add(privilege);
//        roles.forEach(role -> {
//            role.setPrivileges(privileges);
//        });
        return  ResponseEntity.ok(user);
    }
    @DeleteMapping("/delete")
    public  ResponseEntity<?> deletePermission(@RequestBody UserDemo userDemo) {
        User user = userRepo.getUserByName(userDemo.getName());
        Collection<Role> roles = user.getRoles();
        Collection<String> privileges = userDemo.getPrivileges();
        Collection<Privilege> listPrivileges = new ArrayList<>();
        privileges.forEach(privilege -> {
            Privilege pri =privilegeRepo.findByName(privilege);
            if(pri!=null) {
                listPrivileges.add(pri);
            }

        });
        roles.forEach(role -> {
            role.setPrivileges(listPrivileges);
            roleRepo.save(role);
        });
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("Remove")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<?> removeRole(@RequestBody UserDemo userDemo) {
        List<Role> roles = roleRepo.findId(userDemo.getId());
        Privilege p = privilegeRepo.findByName(userDemo.getPrivilege());
        roles.forEach(role -> {
            Collection<Privilege> privileges = role.getPrivileges();
            Iterator<Privilege> pri = privileges.iterator();
            while(pri.hasNext()) {
                Privilege pro =pri.next();
                if(pro.getName() == p.getName()) {
                    privileges.remove(pro);
                }
            }
            roleRepo.save(role);
        });
        User user = userRepo.getUserById(userDemo.getId());
        return ResponseEntity.ok(user);
    }
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('USER_READ')")
    public  ResponseEntity<?> getUser(@RequestParam(value = "name",required = false) String name
            ,@RequestParam(value = "page") Optional<Integer> page
            ,@RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(3);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("userName"));
        Page<User> resultPage = null;
        if (StringUtils.hasText(name)) {
            resultPage = userService.findByUserNameContaining(name, pageable);

        } else {
            resultPage = userRepo.findAll(pageable);
        }
        return ResponseEntity.ok(resultPage);
    }
<<<<<<< HEAD
    @Transactional
    @GetMapping("/deleteId")
    @PreAuthorize("hasAuthority('USER_READ')")
    public  ResponseEntity<?> deleteRole(@RequestParam(value = "roleId") Integer roleId,@RequestParam(value = "name") String name) {
        roleRepo.deleteRoleId(roleId,name);
        return ResponseEntity.ok("Delete access");
=======
    @GetMapping("/deleteByid")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<?> deleteRoleId(@RequestParam(value = "roleId") Integer roleId,@RequestParam(value = "name") String name) {
        List<Role> roles = roleRepo.findId(roleId);
        Privilege privilege = privilegeRepo.findByName(name);
        if(roles == null) {
            return  ResponseEntity.ok("Role null");
        } else {
            if(privilege == null) {
                return ResponseEntity.ok("Privilege null");
            } else {
                roles.forEach(role -> {
                    Collection<Privilege> privileges = role.getPrivileges();
                    Iterator<Privilege> pri = privileges.iterator();
                    while(pri.hasNext()) {
                        Privilege pro =pri.next();
                        if(pro.getName() == privilege.getName()) {
                            privileges.remove(pro);
                        }
                    }
                    roleRepo.save(role);
                });
                return ResponseEntity.ok("Delete success");
            }
        }
>>>>>>> branch3
    }
}



