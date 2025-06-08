package com.evaluacion.controller;


import com.evaluacion.usuariosapi.api.UserApi;
import com.evaluacion.usuariosapi.dto.PublicUserResponse;
import com.evaluacion.usuariosapi.dto.UserRequest;
import com.evaluacion.usuariosapi.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController implements UserApi {



    @Override
    public ResponseEntity<UserResponse> createUser(UserRequest userRequest) {

        return null;
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
