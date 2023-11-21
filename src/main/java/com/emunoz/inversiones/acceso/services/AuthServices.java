package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.loginRequest.LoginRequestDTO;
import com.emunoz.inversiones.acceso.models.loginRequest.RevokedTokenRequestDTO;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserLoginResponseDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AuthServices  {

    UserLoginResponseDTO searchUserByCredentials(LoginRequestDTO loginRequestDTO);

    UserResponseDTO logoutService (String token);

}
