package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.loginRequest.LoginRequestDTO;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import org.springframework.http.ResponseEntity;

public interface AuthServices  {

    ResponseEntity<Object> SearchUserByCredentials(LoginRequestDTO loginRequestDTO);

}
