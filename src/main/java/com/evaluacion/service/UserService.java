package com.evaluacion.service;

import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.entity.User;

import java.util.UUID;

public interface UserService {
    User createUser(UserRequestDTO dto);
    User getUser(UUID id);
    User updateUser(UUID id, UserRequestDTO dto);
    User partiallyUpdateUser(UUID id, UserRequestDTO dto);
    void deleteUser(UUID id);
    void updateLastLogin(UUID id);
}
