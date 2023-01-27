package com.example.demo_jwt.repostory;

import com.example.demo_jwt.enitity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepostory extends JpaRepository<Privilege,Integer> {
    Privilege findByName(String name);
    @Query("select u From Privilege u where u.id =?1")
    Privilege findId(Integer id);
}
