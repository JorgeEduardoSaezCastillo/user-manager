package com.evaluacion.validation;

import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidationUser {

    @Value("${validacion.regex.correo}")
    private String regexCorreo;

    @Value("${validacion.regex.contrasena}")
    private String regexContrasena;

    public void validate(UserRequestDTO dto){
        if(dto.getName() == null || dto.getName().isBlank()){
            throw new ValidationException("El nombre es obligatorio");
        }

        if(dto.getEmail() == null || dto.getEmail().isBlank()){
            throw new ValidationException("El correo es obligatorio");
        }

        if(!Pattern.matches(regexCorreo, dto.getEmail())){
            throw new ValidationException("El formato del correo no es válido");
        }

        if(dto.getPassword() == null || dto.getPassword().isBlank()){
            throw new ValidationException("La contraseña es obligatoria");
        }

        if(!Pattern.matches(regexContrasena, dto.getPassword())){
            throw new ValidationException("La contraseña no cumple con el formato requerido");
        }

        if(dto.getPhones() == null || dto.getPhones().isEmpty()){
            throw new ValidationException("Debe proporcionar al menos un teléfono");
        }
    }
}
