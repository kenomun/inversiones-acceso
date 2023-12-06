package com.emunoz.inversiones.acceso.controllers;

import com.emunoz.inversiones.acceso.Validation.ValidationUtils;
import com.emunoz.inversiones.acceso.models.loginRequest.LoginRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserLoginResponseDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.services.AuthServices;
import com.emunoz.inversiones.acceso.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/V1")
public class AuthController {

    @Autowired
    private  AuthServices authServices;

    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private JWTUtil jwtUtil;



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
    @PostMapping(path = "/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Validated @RequestBody LoginRequestDTO loginRequestDTO, BindingResult bindingResult) {

        ResponseEntity<UserLoginResponseDTO> validationError = validationUtils.handleValidationLoginErrors(bindingResult);
        if (validationError != null) {
            return validationError;
        }

        UserLoginResponseDTO res = authServices.searchUserByCredentials(loginRequestDTO);

        if (res.getCode() == 0) {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else if (res.getCode() == 1) {
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        } else if (res.getCode() == 2){
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/revoke-token")
    public ResponseEntity<UserResponseDTO> Logo(@RequestHeader(name = "Authorization") String token) {
        if(jwtUtil.getPermission(token) != 2) {
            UserResponseDTO res = new UserResponseDTO();
            res.setMessage("Usuario no autorizado.");
            res.setCode(0);
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }

        UserResponseDTO res = authServices.logoutService(token);

        if (res.getCode() == 2){
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
