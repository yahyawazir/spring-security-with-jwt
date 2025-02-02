package com.yahya.security.repo;

import com.yahya.security.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface UserRepository extends MongoRepository<Users, String> {
    Users findByEmail(String email);
}
