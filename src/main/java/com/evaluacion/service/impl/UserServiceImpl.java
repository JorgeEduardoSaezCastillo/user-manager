package com.evaluacion.service.impl;

import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.entity.User;
import com.evaluacion.mapper.UserMapper;
import com.evaluacion.repository.UserRepository;
import com.evaluacion.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User createUser(UserRequestDTO dto) {

        User user = UserMapper.toEntity(dto);
        User created = userRepository.save(user);
        LocalDateTime now = LocalDateTime.now();
        created.setCreated(now);
        created.setLastLogin(now);
        created.setActive(true);

        return userRepository.save(created);
    }
}

