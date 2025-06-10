package com.evaluacion.validation;

import com.evaluacion.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ValidationGetUser {

    public void validate(UUID id) {
        if (id == null) {
            throw new ValidationException("El ID del usuario no puede ser nulo");
        }

        if (id.equals(new UUID(0, 0))) {
            throw new ValidationException("El ID del usuario no puede ser un UUID vacío");
        }
    }
}
