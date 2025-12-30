package com.vsms.user.repository;

import com.vsms.user.entity.User;
import com.vsms.user.enums.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
    List<User> findByStatus(UserStatus status);

    List<User> findByRoleAndStatus(Role role, UserStatus status);

//    List<User> findByRoleAndManagerId(Role role, String managerId);
}
