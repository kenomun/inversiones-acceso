package com.emunoz.inversiones.acceso.controllers;


import com.emunoz.inversiones.acceso.Validation.ValidationUtils;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/V1/usuario")
public class UserController {
    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService){
        this.userService = userService;
    }

    @Autowired
    private ValidationUtils validationUtils;
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
    public ResponseEntity<Object> getUsers() {
        return userService.getUsers();
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
            }
    )
    @GetMapping (path = "{usuarioId}")
    public ResponseEntity<Object> getUser(@PathVariable("usuarioId") Long id) {
        return userService.getUserById(id);
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
    public ResponseEntity<Object> createUser(@Validated @RequestBody UserRequestDTO userRequest, BindingResult bindingResult) {
        ResponseEntity<Object> validationError = validationUtils.handleValidationErrors(bindingResult);
        if (validationError != null) {
            return validationError;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setName(userRequest.getEmail());

        ResponseEntity<Object> result = userService.createUser(userRequest);

        return new ResponseEntity<>(result.getBody(), result.getStatusCode());
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
    public ResponseEntity<Object> updateProduct(@Validated @RequestBody UserRequestDTO userRequest, BindingResult bindingResult){
        ResponseEntity<Object> validationError = validationUtils.handleValidationErrors(bindingResult);
        if (validationError != null) {
            return validationError;
        }

        ResponseEntity<Object> result = userService.updateUser(userRequest);
        return new ResponseEntity<>(result.getBody(), result.getStatusCode());
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
    public ResponseEntity<Object> deleteUser(@PathVariable("usuarioId") Long UserId, @RequestHeader(name = "Authorization") String token){
        return this.userService.deleteUser(UserId, token);

    }
}
