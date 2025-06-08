package com.evaluacion.service.impl;

import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.entity.User;
import com.evaluacion.exception.ValidationException;
import com.evaluacion.mapper.PhoneMapper;
import com.evaluacion.mapper.UserMapper;
import com.evaluacion.repository.UserRepository;
import com.evaluacion.config.JwtUtil;
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
    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
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
        // persistir
        User created = userRepository.save(user);

        String token = jwtUtil.generarToken(created.getId());
        LocalDateTime now = LocalDateTime.now();

        created.setToken(token);
        created.setCreated(now);
        created.setLastLogin(now);
        created.setActive(true);

        // persistir con token y timestamps
        return userRepository.save(created);
    }

    @Override
    @Transactional
    public User getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuario no encontrado"));

        UUID idAutenticado = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        updateLastLogin(idAutenticado);

        return user;
    }

    @Override
    public User updateUser(UUID id, UserRequestDTO dto) {
        validatePropietario(id);
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuario no encontrado"));

        if (userRepository.existsByEmail(dto.getEmail()) && !existente.getEmail().equals(dto.getEmail())) {
            throw new ValidationException("El correo ya está registrado por otro usuario");
        }

        existente.setName(dto.getName());
        existente.setEmail(dto.getEmail());
        existente.setPassword(dto.getPassword());
        existente.setModified(LocalDateTime.now());
        existente.setLastLogin(LocalDateTime.now());

        existente.getPhones().clear();
        if (dto.getPhones() != null) {
            existente.getPhones().addAll(PhoneMapper.mapearDesdeDTOs(dto.getPhones(), existente));
        }

        updateLastLogin(id);
        return userRepository.save(existente);
    }

    @Override
    @Transactional
    public User partiallyUpdateUser(UUID id, UserRequestDTO dto) {
        validatePropietario(id);
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuario no encontrado"));

        if (dto.getName() != null) existente.setName(dto.getName());

        if (dto.getEmail() != null) {
            if (userRepository.existsByEmail(dto.getEmail()) && !existente.getEmail().equals(dto.getEmail())) {
                throw new ValidationException("El correo ya está registrado por otro usuario");
            }
            existente.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) existente.setPassword(dto.getPassword());

        if (dto.getPhones() != null) {
            existente.getPhones().clear();
            existente.getPhones().addAll(PhoneMapper.mapearDesdeDTOs(dto.getPhones(), existente));
        }

        existente.setModified(LocalDateTime.now());
        updateLastLogin(id);

        return userRepository.save(existente);
    }

    @Override
    public void deleteUser(UUID id) {
        validatePropietario(id);
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuario no encontrado"));

        userRepository.delete(existente);
    }

    @Override
    public void updateLastLogin(UUID idUser) {
        userRepository.findById(idUser).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    private void validatePropietario(UUID idUserSolicitado) {
        UUID idAutenticado = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        if (!idAutenticado.equals(idUserSolicitado)) {
            throw new ValidationException("No tiene permisos para modificar este recurso");
        }
    }
}

