package com.evaluacion.controller;


import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.entity.User;
import com.evaluacion.exception.ValidationException;
import com.evaluacion.mapper.UserMapper;
import com.evaluacion.service.UserService;
import com.evaluacion.usuariosapi.api.UserApi;
import com.evaluacion.usuariosapi.dto.PublicUserResponse;
import com.evaluacion.usuariosapi.dto.UserRequest;
import com.evaluacion.usuariosapi.dto.UserResponse;
import com.evaluacion.validation.ValidationUser;
import com.evaluacion.validation.ValidationUserDelete;
import com.evaluacion.validation.ValidationUserPatch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController implements UserApi {

    private final UserService userService;
    private final ValidationUser validationUser;
    private final ValidationUserDelete validationUserDelete;
    private final ValidationUserPatch validationUserPatch;



    public UserController(UserService userService,
                          ValidationUser validationUser,
                          ValidationUserDelete validationUserDelete,
                          ValidationUserPatch validationUserPatch) {
        this.userService = userService;
        this.validationUser = validationUser;
        this.validationUserDelete = validationUserDelete;
        this.validationUserPatch = validationUserPatch;
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
        validationUserDelete.validate(id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PublicUserResponse> getUser(UUID id) {
        User usuario = userService.getUser(id);
        PublicUserResponse response = UserMapper.toPublicResponse(usuario);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserResponse> partiallyUpdateUser(UUID id, UserRequest userRequest) {
        UserRequestDTO dto = UserMapper.toRequestDTO(userRequest);
        validationUserPatch.validate(dto);
        User actualizado = userService.partiallyUpdateUser(id, dto);
        UserResponse response = UserMapper.toResponse(actualizado);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(UUID id, UserRequest userRequest) {
        UserRequestDTO dto = UserMapper.toRequestDTO(userRequest);
        validationUser.validate(dto);
        User actualizado = userService.updateUser(id,dto);
        UserResponse response = UserMapper.toResponse(actualizado);
        return ResponseEntity.ok(response);
    }
}
