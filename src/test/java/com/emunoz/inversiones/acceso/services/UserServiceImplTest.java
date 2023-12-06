package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.entity.RoleEntity;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.repositry.RoleRepository;
import com.emunoz.inversiones.acceso.repositry.UserRepository;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService; // Esto inyectará automáticamente las dependencias con anotaciones @Mock

    @Mock
    private UserRepository userRepository; // Esto crea un mock del repositorio para usar en la prueba

    @Mock
    private RoleRepository roleRepository;

    @Test
    public void getUsersAll_userNotFound() {

        when(userRepository.findAll()).thenReturn(Lists.newArrayList());
        UserResponseDTO rsp = userService.getUsersAll();

        //Asegura que no existen usuarios registrados.
        assertEquals("No hay usuarios registrados.", rsp.getMessage());
        assertEquals(rsp.getCode(), 1);
    }

    @Test
    public void getUsersAll_userFound() {

        when(userRepository.findAll()).thenReturn(Lists.newArrayList(UserEntity.builder().build()));
        UserResponseDTO rsp = userService.getUsersAll();

        //Asegura que haya encontrado usuarios.
        assertEquals("Usuarios encontrados.", rsp.getMessage());
        assertEquals(rsp.getCode(), 2);

    }

    @Test
    public void getUserById_userNotFound() {

        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        UserResponseDTO rsp = userService.getUserById(userId);
        // Asegura que el usuario no existe.
        assertEquals("No existe el usuario.", rsp.getMessage());
        assertEquals(rsp.getCode(), 1);
    }

    @Test
    public void getUserById_userFound() {

        long userId = 1L;
        UserEntity user = UserEntity.builder()
                .id(userId)
                .name("John Doe")
                .email("johndoe@example.com")
                .password("321654")
                .state("activo")
                .role(RoleEntity.builder()
                        .description("User")
                        .permission(1)
                        .build())
                .build();

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        UserResponseDTO rsp = userService.getUserById(userId);
        // Asegúrate la existencia del usuario.
        assertEquals("Usuario encontrado.", rsp.getMessage());
        assertEquals(rsp.getCode(), 2);
    }

    @Test
    public void createUser_EmailAlreadyExists() {

        String email = "usuario@gmail.com";
        UserRequestDTO userRequest = UserRequestDTO.builder()
                .name("usuario")
                .email(email)
                .password("321654")
                .state("activo")
                .build();

        // Simula que ya existe un usuario con el mismo correo en la base de datos
        when(userRepository.findUserByEmail(userRequest.getEmail())).thenReturn(Optional.of(new UserEntity()));
        UserResponseDTO rsp = userService.createUser(userRequest);
        // Asegúrate de ya existe un usuario con el mismo correo.
        assertEquals("El email ya esta registrado.", rsp.getMessage());
        assertEquals(1, rsp.getCode());
    }

    @Test
    public void createUserDeafulRole_Success() {
        RoleEntity role = RoleEntity.builder()
                .id(1)
                .description("user")
                .permission(1)
                .build();

        UserRequestDTO userRequest = UserRequestDTO.builder()
                .name("usuario")
                .email("usuario@gmail.com")
                .password("321654")
                .build();

        when(userRepository.findUserByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        UserResponseDTO rsp = userService.createUser(userRequest);
        // Asegura que el usuario se haya creado con éxisto con role por defecto
        assertEquals("Usuario creado con éxito.", rsp.getMessage());
        assertEquals(2, rsp.getCode());
    }

    @Test
    public void createUserWhitRole_Success() {

        RoleEntity role = RoleEntity.builder()
                .id(1)
                .description("user")
                .permission(1)
                .build();

        UserRequestDTO userRequest = UserRequestDTO.builder()
                .name("usuario")
                .email("usuario@gmail.com")
                .password("321654")
                .role_id(2)
                .build();

        when(userRepository.findUserByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(userRequest.getRole_id())).thenReturn(Optional.of(role));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        UserResponseDTO rsp = userService.createUser(userRequest);
        // Asegura que el usuario se haya creado con éxisto enviandole el role.
        assertEquals("Usuario creado con éxito.", rsp.getMessage());
        assertEquals(2, rsp.getCode());
    }


    @Test
    public void updateUser_userNotFound() {

        Long id = 1L;
        UserRequestDTO userRequest = UserRequestDTO.builder()
                .id(id)
                .name("usuario")
                .email("usuario@gmail.com")
                .password("321654")
                .state("inactivo")
                .role_id(2)
                .build();

        // Simula que no existe ningún usuario con el mismo correo en la base de datos
        when(userRepository.findUserById(id)).thenReturn(Optional.empty());
        UserResponseDTO rsp = userService.updateUser(userRequest);

        // Asegúrate de que el usuario no existe.
        assertEquals("Usuario no existe.", rsp.getMessage());
        assertEquals(0, rsp.getCode());
    }

    @Test
    public void updateUser_EmailAlreadyExists() {

        Long id = 1L;
        UserRequestDTO userRequest = UserRequestDTO.builder()
                .id(id)
                .name("usuario")
                .email("usuario@gmail.com")
                .password("321654")
                .state("inactivo")
                .build();


        when(userRepository.findUserById(id)).thenReturn(Optional.of(new UserEntity())); // Simula la existencia de un usuario con el id.
        when(userRepository.findUserByEmail(userRequest.getEmail())).thenReturn(Optional.of(new UserEntity())); // Simula la existencia de un usuario con el mismo email.
        UserResponseDTO rsp = userService.updateUser(userRequest);

        // Asegúrate de ya existe un usuario con el mismo correo.
        assertEquals("El correo ya existe en otro usuario.", rsp.getMessage());
        assertEquals(1, rsp.getCode());
    }

    @Test
    public void updateUser_Success() {

        RoleEntity role = RoleEntity.builder()
                .id(1)
                .description("user")
                .permission(1)
                .build();

        Long id = 1L;
        UserRequestDTO userRequest = UserRequestDTO.builder()
                .id(id)
                .name("usuario")
                .email("usuario@gmail.com")
                .password("321654")
                .state("inactivo")
                .role_id(1)
                .build();

        when(userRepository.findUserById(id)).thenReturn(Optional.of(new UserEntity())); // Simula la existencia de un usuario con el mismo id.
        when(userRepository.findUserByEmail(userRequest.getEmail())).thenReturn(Optional.empty()); // Simula que no existe ningún usuario con el mismo correo en la base de datos.
        when(roleRepository.findById(userRequest.getRole_id())).thenReturn(Optional.of(role));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity userToSave = invocation.getArgument(0);
            return userToSave;
        });
        UserResponseDTO rsp = userService.updateUser(userRequest);
        // Asegúrate de que el usuario se haya actualizado con éxito.
        assertEquals("Usuario actualizado con éxito.", rsp.getMessage());
        assertEquals(2, rsp.getCode());
    }

    @Test
    public void deleteUser_userNotFound() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false); // Simula que el usuario no existe
        UserResponseDTO rsp = userService.deleteUser(userId);

        // Asegúrate de que el usuario no existe.
        assertEquals("No existe el usuario.", rsp.getMessage());
        assertEquals(1, rsp.getCode());
    }

    @Test
    public void deleteUser_Success() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true); // Simula que el usuario no existe
        UserResponseDTO rsp = userService.deleteUser(userId);

        // Asegúrate de que el usuario se haya eliminado con éxito
        assertEquals("Usuario eliminado exitosamente.", rsp.getMessage());
        assertEquals(2, rsp.getCode());
    }

}

