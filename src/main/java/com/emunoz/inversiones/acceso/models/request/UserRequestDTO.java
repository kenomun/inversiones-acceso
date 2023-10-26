package com.emunoz.inversiones.acceso.models.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserRequestDTO {

    private Long id;

    @NotBlank(message = "El nombre no debe estar vacío")
    @Size(max = 20)
    private String name;

    @NotBlank(message = "El email no debe estar vacío")
    private String email;

    @NotBlank(message = "El password no debe estar vacío")
    private String password;

    private String state;

    private Integer role_id;

    public UserRequestDTO() {
    }

    public UserRequestDTO(Long id, String email, String password, String state, Integer role_id) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.state = state;
        this.role_id = role_id;
    }
}
