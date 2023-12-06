package com.emunoz.inversiones.acceso.models.loginRequest;

import lombok.Data;

@Data
public class RevokedTokenRequestDTO {
    private String token;

    public RevokedTokenRequestDTO(String token) {
        this.token = token;
    }
}
