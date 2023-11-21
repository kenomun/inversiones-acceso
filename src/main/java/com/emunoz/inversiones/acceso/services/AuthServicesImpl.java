package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.entity.RevokedTokenEntity;
import com.emunoz.inversiones.acceso.models.entity.RoleEntity;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.loginRequest.LoginRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserDataResponseDTO;
import com.emunoz.inversiones.acceso.models.response.UserLoginResponseDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.repositry.LogoutRepository;
import com.emunoz.inversiones.acceso.repositry.UserRepository;
import com.emunoz.inversiones.acceso.userMapper.UserMapper;
import com.emunoz.inversiones.acceso.util.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class AuthServicesImpl implements AuthServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LogoutRepository logoutRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public UserLoginResponseDTO searchUserByCredentials(LoginRequestDTO loginRequestDTO) {
        UserLoginResponseDTO userLoginResponse = new UserLoginResponseDTO();

        Optional<UserEntity> user = userRepository.findUserByEmail(loginRequestDTO.getEmail());

        if (!user.isPresent()) {
            userLoginResponse.setMessage("Usuario no existe.");
            userLoginResponse.setCode(0);
            return userLoginResponse;
        }

        UserEntity userEntity = user.get();
        String passwordHashed = userEntity.getPassword();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        boolean verifyPassword = argon2.verify(passwordHashed, loginRequestDTO.getPassword()); // Verificar si la contraseña es válida


        if (!verifyPassword) {
            userLoginResponse.setMessage("Password Incorrecto.");
            userLoginResponse.setCode(1);
            return userLoginResponse;
        }

        RoleEntity userRole = userEntity.getRole();
        UserDataResponseDTO userdataResponse = UserMapper.toResponseDTO(userEntity);
        userdataResponse.setRoleDescription(userRole.getDescription());
        userdataResponse.setRolePermission(userRole.getPermission());

        String token = jwtUtil.create(String.valueOf(userdataResponse.getId()), userdataResponse.getEmail(), String.valueOf(userdataResponse.getRolePermission()));

        userLoginResponse.setMessage("Usuario logeado.");
        userLoginResponse.setToken(token);
        userLoginResponse.setData(userdataResponse);
        userLoginResponse.setCode(2);


        return userLoginResponse;
    }

    @Override
    public UserResponseDTO logoutService(String token) {
        // Esta lista almacenaría los tokens inválidos o revocados
        UserResponseDTO userResponse = new UserResponseDTO();

            // Agregar el token a la lista de tokens inválidos
        RevokedTokenEntity revokedTokenEntity = UserMapper.toRevokesToken(token);
        logoutRepository.save(revokedTokenEntity);

        userResponse.setMessage("Usuario deslogeado exitosamente.");
        userResponse.setCode(2);
        return userResponse;
    }
}
