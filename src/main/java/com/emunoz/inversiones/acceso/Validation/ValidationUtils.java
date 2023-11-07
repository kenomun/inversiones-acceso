package com.emunoz.inversiones.acceso.Validation;

import com.emunoz.inversiones.acceso.models.response.UserLoginResponseDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
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

    public ResponseEntity<UserResponseDTO> handleValidationErrors(BindingResult bindingResult) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }

            for (ObjectError error : bindingResult.getGlobalErrors()) {
                errors.add(error.getDefaultMessage());
            }
            userResponseDTO.setMessage("Campos vacios");
            userResponseDTO.setData(errors);
            userResponseDTO.setCode(0);
            return new ResponseEntity<>(userResponseDTO, HttpStatus.BAD_REQUEST);
        }

        return null; // No hay errores de validación
    }

    public ResponseEntity<UserLoginResponseDTO> handleValidationLoginErrors(BindingResult bindingResult) {
        UserLoginResponseDTO userloginResponseDTO = new UserLoginResponseDTO();

        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }

            for (ObjectError error : bindingResult.getGlobalErrors()) {
                errors.add(error.getDefaultMessage());
            }
            userloginResponseDTO.setMessage("Campos vacios");
            userloginResponseDTO.setData(errors);
            userloginResponseDTO.setCode(0);
            return new ResponseEntity<>(userloginResponseDTO, HttpStatus.BAD_REQUEST);
        }

        return null; // No hay errores de validación
    }

}
