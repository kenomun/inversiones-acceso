package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.entity.RevokedTokenEntity;
import com.emunoz.inversiones.acceso.models.entity.RoleEntity;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.loginRequest.LoginRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserLoginResponseDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.repositry.LogoutRepository;
import com.emunoz.inversiones.acceso.repositry.UserRepository;

import com.emunoz.inversiones.acceso.util.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {

    @InjectMocks
    private AuthServicesImpl authServices;

    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private UserRepository userRepository;

    @Mock
    private LogoutRepository logoutRepository;


    @Test
    public void searchUserByCredentials_userNotFound() {

        LoginRequestDTO userLogin = LoginRequestDTO.builder()
                .email("usuario@gmail.com")
                .password("321654")
                .build();

        when(userRepository.findUserByEmail(userLogin.getEmail())).thenReturn(Optional.empty());
        UserLoginResponseDTO rsp = authServices.searchUserByCredentials(userLogin);

        //Asegura que haya encontrado usuarios.
        assertEquals("Usuario no existe.", rsp.getMessage());
        assertEquals(rsp.getCode(), 0);
    }


    @Test
    public void searchUserByCredentials_incorrectPassword() {

        UserEntity user = UserEntity.builder()
                .name("usuario")
                .email("usuario@gmail.com")
                .password("321654")
                .state("Activo")
                .role(RoleEntity.builder()
                        .description("usuario")
                        .permission(1)
                        .build())
                .build();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hash = argon2.hash(1, 1024, 1, user.getPassword());
        user.setPassword(hash);

        LoginRequestDTO userLogin = LoginRequestDTO.builder()
                .email("usuario@gmail.com")
                .password("321655")
                .build();

        when(userRepository.findUserByEmail(userLogin.getEmail())).thenReturn(Optional.of(user));
        UserLoginResponseDTO rsp = authServices.searchUserByCredentials(userLogin);
        // Asegura que la contraseña sea incorrecta.
        assertEquals("Password Incorrecto.", rsp.getMessage());
        assertEquals(1, rsp.getCode());
    }

    @Test
    public void searchUserByCredentials_Success() {

        UserEntity user = UserEntity.builder()
                .name("usuario")
                .email("usuario@gmail.com")
                .password("321654")
                .state("Activo")
                .role(RoleEntity.builder()
                        .description("usuario")
                        .permission(1)
                        .build())
                .build();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hash = argon2.hash(1, 1024, 1, user.getPassword());
        user.setPassword(hash);


        LoginRequestDTO userLogin = LoginRequestDTO.builder()
                .email("usuario@gmail.com")
                .password("321654")
                .build();

        when(userRepository.findUserByEmail(userLogin.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.create(String.valueOf(user.getId()), user.getEmail(), String.valueOf(user.getRole().getPermission()))).thenReturn("token_de_prueba");
        UserLoginResponseDTO rsp = authServices.searchUserByCredentials(userLogin);

        // Asegura que la contraseña sea incorrecta.
        assertEquals("Usuario logeado.", rsp.getMessage());
        assertEquals(2, rsp.getCode());
    }

    //----------------------------------


    @Test
    public void logoutService_Success() {
        String token = "token";

        when(logoutRepository.findByToken(token)).thenReturn(Optional.empty());
        UserResponseDTO rsp = authServices.logoutService(token);

        assertEquals("Usuario deslogeado exitosamente.", rsp.getMessage());
        assertEquals(2, rsp.getCode());


    }



}
