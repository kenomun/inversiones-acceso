package com.emunoz.inversiones.acceso.controllers;

import com.emunoz.inversiones.acceso.Validation.ValidationUtils;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.services.UserService;
import com.emunoz.inversiones.acceso.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ValidationUtils validationUtils;

    @MockBean
    private JWTUtil jwtUtil;


    @Test
    public void GetUsersAll_Unauthorized() throws Exception {

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válido
        when(jwtUtil.getPermission("0")).thenReturn(0);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(get("/api/V1/usuario").header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no autorizado."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void GetUsersAll_NotFound() throws Exception {

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("No hay usuarios registrados.")
                .code(1)
                .build();

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getPermission("token")).thenReturn(2);
        when(userService.getUsersAll()).thenReturn(userResponseDTO);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(get("/api/V1/usuario").header("Authorization","token"))
                .andExpect(jsonPath("$.message").value("No hay usuarios registrados."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void GetUsersAll_UsersFound() throws Exception {

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("Usuarios encontrado.")
                .code(2)
                .build();

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getPermission("token")).thenReturn(2);
        when(userService.getUsersAll()).thenReturn(userResponseDTO);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(get("/api/V1/usuario").header("Authorization","token"))
                .andExpect(jsonPath("$.message").value("Usuarios encontrado."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }

    // ----------------------------------------
    @Test
    void getUserById_Unauthorized() throws Exception {
        Long userId = 1L;

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getPermission("token")).thenReturn(0);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(get("/api/V1/usuario/{usuarioId}", userId).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no autorizado."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserById_NotFound() throws Exception {

        Long userId = 1L;
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("No existe el usuario.")
                .code(1)
                .build();

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getPermission("token")).thenReturn(2);
        when(userService.getUserById(userId)).thenReturn(userResponseDTO);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(get("/api/V1/usuario/{usuarioId}", userId).header("Authorization","token"))
                .andExpect(jsonPath("$.message").value("No existe el usuario."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_Success() throws Exception {

        Long userId = 1L;
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("Usuario encontrado.")
                .code(2)
                .build();

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getPermission("token")).thenReturn(2);
        when(userService.getUserById(userId)).thenReturn(userResponseDTO);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(get("/api/V1/usuario/{usuarioId}", userId).header("Authorization","token"))
                .andExpect(jsonPath("$.message").value("Usuario encontrado."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }

    // ----------------------------------------
    @Test
    void createUser_EmailExist() throws Exception {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("Usuario")
                .email("Usuario@gmail.com")
                .password("123456")
                .build();

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("El email ya esta registrado.")
                .code(1)
                .build();

        when(userService.createUser(userRequestDTO)).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/V1/usuario").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userRequestDTO)))
                .andExpect(jsonPath("$.message").value("El email ya esta registrado."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isConflict());
    }

    @Test
    void createUser_success() throws Exception {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("Usuario")
                .email("Usuario@gmail.com")
                .password("123456")
                .build();

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("Usuario creado con éxito.")
                .code(2)
                .build();

        when(userService.createUser(userRequestDTO)).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/V1/usuario").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userRequestDTO)))
                .andExpect(jsonPath("$.message").value("Usuario creado con éxito."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }

    // ----------------------------------------

    @Test
    void updateUser_Unauthorized() throws Exception  {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .id(1L)
                .name("Usuario")
                .email("Usuario@gmail.com")
                .build();

        when(jwtUtil.getFullPermission("0",userRequestDTO.getId(),userRequestDTO.getEmail())).thenReturn(1);
        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(put("/api/V1/usuario").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userRequestDTO)).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no autorizado."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void updateProduct_NotFound()throws Exception {

        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .id(1L)
                .name("Usuario")
                .email("Usuario@gmail.com")
                .build();

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("Usuario no existe.")
                .code(0)
                .build();

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getFullPermission("0",userRequestDTO.getId(),userRequestDTO.getEmail())).thenReturn(2);
        when(userService.updateUser(userRequestDTO)).thenReturn(userResponseDTO);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(put("/api/V1/usuario").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userRequestDTO)).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no existe."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isNotFound());

    }

    @Test
    void updateProduct_EmailExist() throws Exception  {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .id(1L)
                .name("Usuario")
                .email("Usuario@gmail.com")
                .build();

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("El correo ya existe en otro usuario.")
                .code(1)
                .build();

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getFullPermission("0",userRequestDTO.getId(),userRequestDTO.getEmail())).thenReturn(2);
        when(userService.updateUser(userRequestDTO)).thenReturn(userResponseDTO);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(put("/api/V1/usuario").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userRequestDTO)).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("El correo ya existe en otro usuario."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isConflict());
    }

    @Test
    void updateProduct_Success() throws Exception  {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .id(1L)
                .name("Usuario")
                .email("Usuario@gmail.com")
                .build();

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("Usuario actualizado con éxito.")
                .code(2)
                .build();

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getFullPermission("0",userRequestDTO.getId(),userRequestDTO.getEmail())).thenReturn(2);
        when(userService.updateUser(userRequestDTO)).thenReturn(userResponseDTO);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(put("/api/V1/usuario").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userRequestDTO)).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario actualizado con éxito."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }

    // ----------------------------------------

    @Test
    void deleteUser_Unauthorized() throws Exception {
        Long userId = 1L;
        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getPermission("token")).thenReturn(1);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(delete("/api/V1/usuario/{usuarioId}", userId).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no autorizado."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void deleteUser_NotFound() throws Exception {
        Long userId = 1L;
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("No existe el usuario.")
                .code(1)
                .build();

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getPermission("token")).thenReturn(2);
        when(userService.deleteUser(userId)).thenReturn(userResponseDTO);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(delete("/api/V1/usuario/{usuarioId}", userId).header("Authorization","token"))
                .andExpect(jsonPath("$.message").value("No existe el usuario."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUser_Success() throws Exception {
        Long userId = 1L;
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("Usuario eliminado exitosamente.")
                .code(2)
                .build();

        // Configura el comportamiento del servicio mock para devolver un código de autorización no válid
        when(jwtUtil.getPermission("token")).thenReturn(2);
        when(userService.deleteUser(userId)).thenReturn(userResponseDTO);

        // Realiza la solicitud HTTP simulada sin un token válido
        mockMvc.perform(delete("/api/V1/usuario/{usuarioId}", userId).header("Authorization","token"))
                .andExpect(jsonPath("$.message").value("Usuario eliminado exitosamente."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }

}