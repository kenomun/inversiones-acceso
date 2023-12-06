package com.emunoz.inversiones.acceso.controllers;

import com.emunoz.inversiones.acceso.Validation.ValidationUtils;
import com.emunoz.inversiones.acceso.models.loginRequest.LoginRequestDTO;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserLoginResponseDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.services.AuthServices;
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

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthServices authServices;

    @MockBean
    private ValidationUtils validationUtils;

    @MockBean
    private JWTUtil jwtUtil;


    @Test
    void login_UserNotFound() throws Exception  {

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email("usuario@example.com")
                .password("321654")
                .build();

        UserLoginResponseDTO userLoginResponseDTO = UserLoginResponseDTO.builder()
                .message("Usuario no existe.")
                .code(0)
                .build();

        when(authServices.searchUserByCredentials(loginRequestDTO)).thenReturn(userLoginResponseDTO);

        mockMvc.perform(post("/api/V1/login").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
                .andExpect(jsonPath("$.message").value("Usuario no existe."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_PasswordError() throws Exception  {

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email("usuario@example.com")
                .password("321654")
                .build();

        UserLoginResponseDTO userLoginResponseDTO = UserLoginResponseDTO.builder()
                .message("Password Incorrecto.")
                .code(1)
                .build();

        when(authServices.searchUserByCredentials(loginRequestDTO)).thenReturn(userLoginResponseDTO);

        mockMvc.perform(post("/api/V1/login").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
                .andExpect(jsonPath("$.message").value("Password Incorrecto."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_Success() throws Exception  {

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email("usuario@example.com")
                .password("321654")
                .build();

        UserLoginResponseDTO userLoginResponseDTO = UserLoginResponseDTO.builder()
                .message("Usuario logeado.")
                .code(2)
                .build();

        when(authServices.searchUserByCredentials(loginRequestDTO)).thenReturn(userLoginResponseDTO);

        mockMvc.perform(post("/api/V1/login").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
                .andExpect(jsonPath("$.message").value("Usuario logeado."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }


    @Test
    void revokeToken_Unauthorized() throws Exception  {

        when(jwtUtil.getPermission("0")).thenReturn(1);

        mockMvc.perform(post("/api/V1/revoke-token").header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no autorizado."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void revokeToken_Success() throws Exception  {

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .message("Usuario deslogeado exitosamente.")
                .code(2)
                .build();

        when(jwtUtil.getPermission("0")).thenReturn(2);
        when(authServices.logoutService("0")).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/V1/revoke-token").header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario deslogeado exitosamente."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }
}