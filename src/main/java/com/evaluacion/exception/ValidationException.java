package com.evaluacion.exception;

public class ValidationException extends RuntimeException{
    public ValidationException(String mensaje){
        super (mensaje);
    }
}
