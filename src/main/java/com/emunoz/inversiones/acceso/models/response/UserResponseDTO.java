package com.emunoz.inversiones.acceso.models.response;

import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String state;
    private Integer role_id;

    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String name, String email, String password, String state, Integer role_id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.state = state;
        this.role_id = role_id;
    }
}
