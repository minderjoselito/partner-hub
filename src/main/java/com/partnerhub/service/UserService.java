package com.partnerhub.service;

import com.partnerhub.domain.User;
import com.partnerhub.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("this email has already been registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

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
