package com.emunoz.inversiones.acceso.models.loginRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
public class LoginRequestDTO {
    @NotBlank(message = "El email no debe estar vacío")
    private String email;

    @NotBlank(message = "El password no debe estar vacío")
    private String password;


}
