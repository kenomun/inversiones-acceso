package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<Object> getUsers();

    ResponseEntity<Object> getUserById(Long id);

    ResponseEntity<Object> createUser(UserRequestDTO userRequest);
    ResponseEntity<Object> updateUser(UserRequestDTO userRequest);

    ResponseEntity<Object> deleteUser(Long id, String token);
}
