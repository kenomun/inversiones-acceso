package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.entity.RoleEntity;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.loginRequest.LoginRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.repositry.UserRepository;
import com.emunoz.inversiones.acceso.userMapper.UserMapper;
import com.emunoz.inversiones.acceso.util.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthServicesImpl implements AuthServices {

    HashMap<String, Object> response;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public ResponseEntity<Object> SearchUserByCredentials(LoginRequestDTO loginRequestDTO) {
        response = new HashMap<>();

        // Buscar al usuario por correo electrónico
        Optional<UserEntity> user = userRepository.findByEmail(loginRequestDTO.getEmail());

        if (!user.isPresent()) {
            response.put("message", "Usuario no existe.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            UserEntity userEntity = user.get();
            String passwordHashed = userEntity.getPassword();

            // Verificar si la contraseña es válida
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            boolean verifyPassword = argon2.verify(passwordHashed, loginRequestDTO.getPassword());

            if (!verifyPassword) {
                response.put("message", "Password Incorrecto.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {

                RoleEntity userRole = userEntity.getRole();
                UserResponseDTO userResponse = UserMapper.toResponseDTO(userEntity);
                userResponse.setRoleDescription(userRole.getDescription());
                userResponse.setRolePermission(userRole.getPermission());


                String token = jwtUtil.create(String.valueOf(userResponse.getId()), userResponse.getEmail(), String.valueOf(userResponse.getRolePermission()));

                jwtUtil.verifyToken(token);

                response.put("message", "Usuario logeado.");
                response.put("token", token);
                response.put("data", userResponse);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }
}
