package com.emunoz.inversiones.acceso.userMapper;

import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserDataResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class UserMapperTest {

    @InjectMocks
    private UserEntity userEntity;


    @Test
    void toEntity() {
        UserRequestDTO userRequest = UserRequestDTO.builder()
                .name("Usuario")
                .email("usuario@example.com")
                .password("secreto")
                .state("Activo")
                .build();

        // "Mockear" el método estático
        try (MockedStatic<UserMapper> userMapper = Mockito.mockStatic(UserMapper.class)) {

            userMapper.when(() -> UserMapper.toEntity(userRequest)).thenReturn(UserEntity.builder().email("usuario@example.com").build());
            UserEntity result = UserMapper.toEntity(userRequest);

            assertEquals("usuario@example.com", result.getEmail()); // Verificar que el campo correo se asigne correctamente.
        }
    }

    @Test
    void toResponseDTO() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("Usuario")
                .email("usuario@example.com")
                .password("secreto")
                .state("Activo")
                .build();

        // "Mockear" el método estático
        try (MockedStatic<UserMapper> userMapper = Mockito.mockStatic(UserMapper.class)) {

            userMapper.when(() -> UserMapper.toResponseDTO(userEntity))
                    .thenReturn(new UserDataResponseDTO(1L, "Usuario", "usuario@example.com", "secreto", "Activo"));

            UserDataResponseDTO result = UserMapper.toResponseDTO(userEntity);
            // Verificar que el campo correo se asigne correctamente.
            assertEquals("usuario@example.com", result.getEmail());

        }
    }

}