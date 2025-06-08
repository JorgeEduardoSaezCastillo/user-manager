package com.evaluacion.validation;

import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidationUserPatch {

    @Value("${validacion.regex.email}")
    private String regexCorreo;

    @Value("${validacion.regex.password}")
    private String regexContrasena;

    public void validate(UserRequestDTO dto){

        if(dto.getName() == null || dto.getName().isBlank()){
            throw new ValidationException("El nombre no puede estar en blanco");
        }

        if(!Pattern.matches(regexCorreo, dto.getEmail())){
            throw new ValidationException("El formato del correo no es válido");
        }

        if(!Pattern.matches(regexContrasena, dto.getPassword())){
            throw new ValidationException("La contraseña no cumple con el formato requerido");
        }
    }
}
