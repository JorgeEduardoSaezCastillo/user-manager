package com.evaluacion.service.impl;

import com.evaluacion.config.JwtUtil;
import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.entity.User;
import com.evaluacion.exception.ValidationException;
import com.evaluacion.mapper.PhoneMapper;
import com.evaluacion.mapper.UserMapper;
import com.evaluacion.repository.UserRepository;
import com.evaluacion.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public User createUser(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("El correo ya está registrado");
        }

        User user = UserMapper.toEntity(dto);
        User created = userRepository.save(user);

        String token = jwtUtil.generarToken(created.getId());
        LocalDateTime now = LocalDateTime.now();

        created.setToken(token);
        created.setCreated(now);
        created.setLastLogin(now);
        created.setActive(true);

        return userRepository.save(created);
    }

    @Override
    public User getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuario no encontrado"));
        return user;
    }

    @Override
    public User updateUser(UUID id, UserRequestDTO dto) {
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuario no encontrado"));

        existente.setName(dto.getName());
        existente.setEmail(dto.getEmail());
        existente.setPassword(dto.getPassword());
        existente.setModified(LocalDateTime.now());
        existente.setLastLogin(LocalDateTime.now());

        existente.getPhones().clear();
        if (dto.getPhones() != null) {
            existente.getPhones().addAll(PhoneMapper.mapearDesdeDTOs(dto.getPhones(), existente));
        }

        return userRepository.save(existente);
    }

    @Override
    @Transactional
    public User partiallyUpdateUser(UUID id, UserRequestDTO dto) {

        User existente = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuario no encontrado"));

        if (dto.getName() != null) existente.setName(dto.getName());

        if (dto.getEmail() != null) {
            existente.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) existente.setPassword(dto.getPassword());

        if (dto.getPhones() != null) {
            existente.getPhones().clear();
            existente.getPhones().addAll(PhoneMapper.mapearDesdeDTOs(dto.getPhones(), existente));
        }

        existente.setModified(LocalDateTime.now());

        return userRepository.save(existente);
    }

    @Override
    public void deleteUser(UUID id) {
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuario no encontrado"));

        userRepository.delete(existente);
    }
}

