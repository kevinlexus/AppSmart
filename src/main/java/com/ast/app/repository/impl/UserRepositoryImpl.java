package com.ast.app.repository.impl;

import com.ast.app.model.Role;
import com.ast.app.model.Status;
import com.ast.app.model.User;
import com.ast.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class UserRepositoryImpl implements UserRepository {

    HashMap<String, User> users = new HashMap<>();

    /**
     * User repository to emulate user's data
     */
    public UserRepositoryImpl() {
        User user = new User();
        user.setEmail("admin@mail.ru");
        user.setPassword("$2y$12$/bqB4e7m5wgNYswTnzxTsevy63wcBSkmXlSe85GTUOY060bIeDvWi");
        user.setRole(Role.ADMIN);
        user.setStatus(Status.ACTIVE);
        users.put(user.getEmail(), user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.of(users.get(email));
    }
}
