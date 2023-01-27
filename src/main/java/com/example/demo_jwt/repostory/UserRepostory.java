package com.example.demo_jwt.repostory;


import com.example.demo_jwt.enitity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepostory extends JpaRepository<User,Integer> {
    @Query("SELECT u FROM User u where u.userName=?1")
    User getUserByName(String username);

    User getUserById(Integer id);
    boolean existsByUserName(String username);
    boolean existsByEmail(String email);
    User deleteById(int id);

    Page<User> findByUserNameContaining(String name, Pageable pageable);

}
