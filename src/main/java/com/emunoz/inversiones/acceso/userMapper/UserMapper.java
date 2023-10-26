package com.emunoz.inversiones.acceso.userMapper;

import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;

public class UserMapper {
    public static UserEntity toEntity(UserRequestDTO userRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userRequest.getName());
        userEntity.setEmail(userRequest.getEmail());
        userEntity.setPassword(userRequest.getPassword());
        userEntity.setState(userRequest.getState());
        userEntity.setRole_id(userRequest.getRole_id());

        return userEntity;
    }

    public static UserResponseDTO toResponseDTO(UserEntity userEntity) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(userEntity.getId());
        responseDTO.setName(userEntity.getName());
        responseDTO.setEmail(userEntity.getEmail());
        responseDTO.setPassword(userEntity.getPassword());
        responseDTO.setState(userEntity.getState());
        responseDTO.setRole_id(userEntity.getRole_id());

        return responseDTO;
    }
}
