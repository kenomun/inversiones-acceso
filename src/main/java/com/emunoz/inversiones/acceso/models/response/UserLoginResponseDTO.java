package com.emunoz.inversiones.acceso.models.response;

import lombok.Data;

@Data
public class UserLoginResponseDTO {

        private String  message;
        private Object data;
        private Integer code;
        private String token;

}
