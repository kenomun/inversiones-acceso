package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.entity.RoleEntity;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.loginRequest.LoginRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserDataResponseDTO;
import com.emunoz.inversiones.acceso.models.response.UserLoginResponseDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.repositry.UserRepository;
import com.emunoz.inversiones.acceso.userMapper.UserMapper;
import com.emunoz.inversiones.acceso.util.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@Log4j2
public class AuthServicesImpl implements AuthServices {

    HashMap<String, Object> response;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public UserLoginResponseDTO SearchUserByCredentials(LoginRequestDTO loginRequestDTO) {
        UserLoginResponseDTO userLoginResponse = new UserLoginResponseDTO();

        // Buscar al usuario por correo electrónico
        Optional<UserEntity> user = userRepository.findByEmail(loginRequestDTO.getEmail());

        if (!user.isPresent()) {
            userLoginResponse.setMessage("Usuario no existe.");
            userLoginResponse.setCode(0);
        } else {
            UserEntity userEntity = user.get();
            String passwordHashed = userEntity.getPassword();

            // Verificar si la contraseña es válida
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            boolean verifyPassword = argon2.verify(passwordHashed, loginRequestDTO.getPassword());
            log.error("Verificacion de password {}",verifyPassword);


            if (!verifyPassword) {
                userLoginResponse.setMessage("Password Incorrecto.");
                userLoginResponse.setCode(1);
            } else {

                RoleEntity userRole = userEntity.getRole();
                UserDataResponseDTO userdataResponse = UserMapper.toResponseDTO(userEntity);
                userdataResponse.setRoleDescription(userRole.getDescription());
                userdataResponse.setRolePermission(userRole.getPermission());


                String token = jwtUtil.create(String.valueOf(userdataResponse.getId()), userdataResponse.getEmail(), String.valueOf(userdataResponse.getRolePermission()));

                jwtUtil.verifyToken(token);

                userLoginResponse.setMessage("Usuario logeado.");
                userLoginResponse.setToken(token);
                userLoginResponse.setData(userdataResponse);
                userLoginResponse.setCode(2);
            }
        }
        return userLoginResponse;
    }
}
