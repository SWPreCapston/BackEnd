package com.precapston.precapston.repository;

import com.precapston.precapston.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByUsername(String username);

    UserEntity findByUsername(String username);

    // username과 userPassword를 검사하는 메서드
    boolean existsByUsernameAndPassword(String username, String password);
}
