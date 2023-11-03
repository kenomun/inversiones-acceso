package com.emunoz.inversiones.acceso.controllers;

import com.emunoz.inversiones.acceso.Validation.ValidationUtils;
import com.emunoz.inversiones.acceso.models.loginRequest.LoginRequestDTO;
import com.emunoz.inversiones.acceso.services.AuthServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/V1/login")
public class AuthController {

    private final AuthServices authServices;

    @Autowired
    public AuthController(AuthServices authServices) {
        this.authServices = authServices;
    }
    @Autowired
    private ValidationUtils validationUtils;


    // ---------------------------------

    @Operation(summary = "login un usuario")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Usuario logeado con éxito", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Error en contraseña", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content),
            }
    )
    @PostMapping
    public ResponseEntity<Object> login(@Validated @RequestBody LoginRequestDTO loginRequestDTO, BindingResult bindingResult) {

        ResponseEntity<Object> validationError = validationUtils.handleValidationErrors(bindingResult);
        if (validationError != null) {
            return validationError;
        }

        ResponseEntity<Object> result = authServices.SearchUserByCredentials(loginRequestDTO);

        // Usuario no existe o error de password
        if(result.getStatusCode() == HttpStatus.NOT_FOUND || result.getStatusCode() == HttpStatus.UNAUTHORIZED){
            return new ResponseEntity<>(result.getBody(), result.getStatusCode());
        }
        // Usuario logeado
        return new ResponseEntity<>(result.getBody(), result.getStatusCode());

    }
}
