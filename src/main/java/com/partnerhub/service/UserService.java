package com.partnerhub.service;

import com.partnerhub.domain.User;
import com.partnerhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    @Transactional
    public User createUser(User user) {
        // TODO: check if email exists, strong password, etc...
        return repository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
