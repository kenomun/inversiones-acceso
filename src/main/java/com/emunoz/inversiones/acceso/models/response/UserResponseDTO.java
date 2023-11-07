package com.emunoz.inversiones.acceso.models.response;

import lombok.Data;

@Data
public class UserResponseDTO {
    private String  message;
    private Object data;
    private Integer code;

}
