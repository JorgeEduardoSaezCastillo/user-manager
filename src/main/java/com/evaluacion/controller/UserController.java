package com.evaluacion.controller;


import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.entity.User;
import com.evaluacion.mapper.UserMapper;
import com.evaluacion.service.UserService;
import com.evaluacion.usuariosapi.api.UserApi;
import com.evaluacion.usuariosapi.dto.PublicUserResponse;
import com.evaluacion.usuariosapi.dto.UserRequest;
import com.evaluacion.usuariosapi.dto.UserResponse;
import com.evaluacion.validation.ValidationUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController implements UserApi {

    private final UserService userService;
    private final ValidationUser validationUser;



    public UserController(UserService userService,
                          ValidationUser validationUser) {
        this.userService = userService;
        this.validationUser = validationUser;
    }

    @Override
    public ResponseEntity<UserResponse> createUser(UserRequest userRequest) {
        UserRequestDTO userDTO = UserMapper.toRequestDTO(userRequest);
        validationUser.validate(userDTO);
        User usuarioCreado = userService.createUser(userDTO);
        UserResponse response = UserMapper.toResponse(usuarioCreado);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<PublicUserResponse> getUser(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<UserResponse> partiallyUpdateUser(UUID id, UserRequest userRequest) {
        return null;
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(UUID id, UserRequest userRequest) {
        return null;
    }
}
