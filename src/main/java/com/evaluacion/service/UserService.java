package com.evaluacion.service;

import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.entity.User;

public interface UserService {
    User createUser(UserRequestDTO dto);
}
