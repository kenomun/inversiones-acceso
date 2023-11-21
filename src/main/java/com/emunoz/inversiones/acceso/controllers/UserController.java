package com.emunoz.inversiones.acceso.controllers;

import com.emunoz.inversiones.acceso.Validation.ValidationUtils;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.services.UserService;
import com.emunoz.inversiones.acceso.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping(path = "api/V1/usuario")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private JWTUtil jwtUtil;
    //-------------------

    @Operation(summary = "Servicio que lista los usuarios")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Usuarios encontrados", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
                    }),
                    @ApiResponse(responseCode = "204", description = "No se encontraron usuarios", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content),
            }
    )
    @GetMapping
    public ResponseEntity<UserResponseDTO> getUsersAll(@RequestHeader(name = "Authorization") String token) {

        if(jwtUtil.getPermission(token) != 2) {
            UserResponseDTO res = new UserResponseDTO();
            res.setMessage("Usuario no autorizado.");
            res.setCode(0);
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }
        UserResponseDTO res = userService.getUsersAll();

        if (res.getCode() == 1){
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else if (res.getCode() == 2){
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //-------------------
    @Operation(summary = "Servicio que lista un usuario")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
                    }),
                    @ApiResponse(responseCode = "204", description = "No se encontro el usuario", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Error ide autorización", content = @Content),
            }
    )
    @GetMapping (path = "{usuarioId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("usuarioId") Long id, @RequestHeader(name = "Authorization") String token) {

        if(jwtUtil.getPermission(token) != 2) {
            UserResponseDTO res = new UserResponseDTO();
            res.setMessage("Usuario no autorizado.");
            res.setCode(0);
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }

        UserResponseDTO res = userService.getUserById(id);

        if (res.getCode() == 1){
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else if (res.getCode() == 2){
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //-------------------
    @Operation(summary = "Agregar un nuevo usuario")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Usuario registrado con éxito", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Solicitud no válida", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Usuario con el mismo email ya existe", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content),
            }
    )
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Validated @RequestBody UserRequestDTO userRequest, BindingResult bindingResult) {



        UserResponseDTO res = userService.createUser(userRequest);

        if (res.getCode() == 1){
            return new ResponseEntity<>(res, HttpStatus.CONFLICT);
        } else if (res.getCode() == 2){
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //-------------------

    @Operation(summary = "Editar un usuario")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content),
            }
    )
    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@Validated @RequestBody UserRequestDTO userRequest, BindingResult bindingResult, @RequestHeader(name = "Authorization") String token){


        if(jwtUtil.getFullPermission(token, userRequest.getId(), userRequest.getEmail()) != 2 ) {
            UserResponseDTO res = new UserResponseDTO();
            res.setMessage("Usuario no autorizado.");
            res.setCode(0);
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }


        ResponseEntity<UserResponseDTO> validationError = validationUtils.handleValidationErrors(bindingResult);
        if (validationError != null) {
            return validationError;
        }

        UserResponseDTO res = userService.updateUser(userRequest);

        if (res.getCode() == 0) {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else if (res.getCode() == 1) {
            return new ResponseEntity<>(res, HttpStatus.CONFLICT);
        } else if (res.getCode() == 2){
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //-------------------

    @Operation(summary = "Eliminar un Usuario")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "Usuario eliminado con éxito", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Usuario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content),
            }
    )
    @DeleteMapping(path = "{usuarioId}")
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable("usuarioId") Long UserId, @RequestHeader(name = "Authorization") String token){

        if(jwtUtil.getPermission(token) != 2) {
            UserResponseDTO res = new UserResponseDTO();
            res.setMessage("Usuario no autorizado.");
            res.setCode(0);
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }

        UserResponseDTO res = userService.deleteUser(UserId);

        if (res.getCode() == 1) {
            return new ResponseEntity<>(res, HttpStatus.CONFLICT);
        } else if (res.getCode() == 2){
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
