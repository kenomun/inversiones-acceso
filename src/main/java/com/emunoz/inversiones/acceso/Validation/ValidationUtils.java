package com.emunoz.inversiones.acceso.Validation;

import org.springframework.http.HttpStatus;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@Component
public class ValidationUtils {

    public ResponseEntity<Object> handleValidationErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }

            for (ObjectError error : bindingResult.getGlobalErrors()) {
                errors.add(error.getDefaultMessage());
            }

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        return null; // No hay errores de validación
    }
}
