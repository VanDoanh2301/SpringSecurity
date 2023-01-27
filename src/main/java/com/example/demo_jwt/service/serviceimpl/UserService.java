package com.example.demo_jwt.service.serviceimpl;

import com.example.demo_jwt.Dto.UserDto;
import com.example.demo_jwt.enitity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    public User saveUser(UserDto userDto);
    public List<User> getAllUser();
    public void deleteUser(int id);

    Page<User> findByUserNameContaining(String name, Pageable pageable);

    void deleteRoleId(Integer id, String name);

    User getUserByName(String username);
}
