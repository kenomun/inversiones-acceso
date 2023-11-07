package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {

    UserResponseDTO getUsersAll();

    UserResponseDTO getUserById(Long id);

    UserResponseDTO createUser(UserRequestDTO userRequest);
    UserResponseDTO updateUser(UserRequestDTO userRequest);

    UserResponseDTO deleteUser(Long id);
}
