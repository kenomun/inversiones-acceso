package com.emunoz.inversiones.acceso.userMapper;

import com.emunoz.inversiones.acceso.models.entity.RevokedTokenEntity;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.loginRequest.RevokedTokenRequestDTO;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserDataResponseDTO;

public class UserMapper {
    public static UserEntity toEntity(UserRequestDTO userRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userRequest.getName());
        userEntity.setEmail(userRequest.getEmail());
        userEntity.setPassword(userRequest.getPassword());
        userEntity.setState(userRequest.getState());

        return userEntity;
    }



    public static UserDataResponseDTO toResponseDTO(UserEntity userEntity) {
        UserDataResponseDTO responseDTO = new UserDataResponseDTO();
        responseDTO.setId(userEntity.getId());
        responseDTO.setName(userEntity.getName());
        responseDTO.setEmail(userEntity.getEmail());
        responseDTO.setPassword(userEntity.getPassword());
        responseDTO.setState(userEntity.getState());

        return responseDTO;
    }

    public static RevokedTokenEntity toRevokesToken(String token) {
        return new RevokedTokenEntity(token);

    }


}
