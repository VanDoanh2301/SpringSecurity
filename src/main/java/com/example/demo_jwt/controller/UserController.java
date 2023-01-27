package com.example.demo_jwt.controller;

import com.example.demo_jwt.Dto.UserDto;
import com.example.demo_jwt.config.jwt.JwtProvider;
import com.example.demo_jwt.enitity.Role;
import com.example.demo_jwt.enitity.User;
import com.example.demo_jwt.payload.AuthorRequest;
import com.example.demo_jwt.payload.AuthorResponse;
import com.example.demo_jwt.repostory.UserRepostory;
import com.example.demo_jwt.service.UserDetailImpl;
import com.example.demo_jwt.service.UserDetailServiceImpl;
import com.example.demo_jwt.service.serviceimpl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepostory userRepo;


    @PostMapping("/signin")
    @PreAuthorize("hasAuthority('USER_SIGNIN')")
    public ResponseEntity<?> login(@RequestBody AuthorRequest authorRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authorRequest.getUsername(),
                        authorRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String tokenn = jwtProvider.generateJwtToken(authentication);
        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
        List<String> roles=userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
        return  ResponseEntity.ok(new AuthorResponse(tokenn,userDetails.getUsername(),roles));
    }

    @PostMapping("/signup")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        if(userRepo.existsByUserName(userDto.getUsername())) {
            return ResponseEntity.badRequest().body("Username is adready taken!");
        }
        if(userRepo.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.badRequest().body("Email is adready taken!");
        }
        User user =  userService.saveUser(userDto);
        Collection<Role> roles = user.getRoles();
        return ResponseEntity.ok(user);

    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<?> getAllUser(@RequestParam("token") String token) {
        String username = jwtProvider.getUserNameFromJwtToken(token);
        User userDetails = userRepo.getUserByName(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails.getUserName(),
                        userDetails.getPassWord());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok(userService.getAllUser());
    }
    @GetMapping("/jwt")
    @PreAuthorize("hasAuthority('USER_READ')")
    public String getToken(@RequestParam("token") String token ) {
        String u = jwtProvider.getUserNameFromJwtToken(token);
              return u;
    }
    @DeleteMapping("/remove_users/{id}")
    @PreAuthorize("hasAuthority('REMOVE_USER')")
    public ResponseEntity<?> removeUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Delete access");
    }
}
