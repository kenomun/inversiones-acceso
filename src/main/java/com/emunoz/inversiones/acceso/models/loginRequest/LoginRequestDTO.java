package com.emunoz.inversiones.acceso.models.loginRequest;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "El email no debe estar vacío")
    private String email;

    @NotBlank(message = "El password no debe estar vacío")
    private String password;


}
