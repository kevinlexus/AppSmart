package com.ast.app.repository;

import com.ast.app.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
}
