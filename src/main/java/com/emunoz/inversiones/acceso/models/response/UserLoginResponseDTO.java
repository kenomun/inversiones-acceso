package com.emunoz.inversiones.acceso.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponseDTO {

        private String  message;
        private Object data;
        private Integer code;
        private String token;

}
