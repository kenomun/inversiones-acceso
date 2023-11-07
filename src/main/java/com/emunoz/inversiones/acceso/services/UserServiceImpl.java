package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.entity.RoleEntity;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserDataResponseDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.repositry.RoleRepository;
import com.emunoz.inversiones.acceso.repositry.UserRepository;
import com.emunoz.inversiones.acceso.userMapper.UserMapper;
import com.emunoz.inversiones.acceso.util.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    HashMap<String, Object> response;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JWTUtil jwtUtil;



    @Autowired
    public UserServiceImpl(UserRepository productRepository) {
        this.userRepository = userRepository;
    }

    //-------------------
    @Override
    public UserResponseDTO getUsersAll() {
        UserResponseDTO userResponse = new UserResponseDTO();

        try {
            List<UserEntity> userEntitiesEntities = this.userRepository.findAll();
            List<UserDataResponseDTO> userDataResponseDTOs = userEntitiesEntities.stream()
                    .map(userEntity -> {
                        UserDataResponseDTO dto = UserMapper.toResponseDTO(userEntity);

                        if (userEntity.getRole() != null) {
                            dto.setRoleDescription(userEntity.getRole().getDescription());
                            dto.setRolePermission(userEntity.getRole().getPermission());
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());

            if (userEntitiesEntities.isEmpty()) {
                userResponse.setMessage("No hay usuarios registrados");
                userResponse.setCode(1);

            }

            userResponse.setMessage("Usuarios encontrados");
            userResponse.setData(userDataResponseDTOs);
            userResponse.setCode(2);
        } catch (DataAccessException ex) {
            userResponse.setMessage(ex.getMessage());
            userResponse.setCode(3);
        }
        return userResponse;
    }

    //-------------------
    @Override
    public UserResponseDTO getUserById(Long id) {

        UserResponseDTO userResponse = new UserResponseDTO();
        try {
            Optional<UserEntity> userOptional = userRepository.findUserById(id);

            if (!userOptional.isPresent()) {
                userResponse.setMessage("No existe el usuario");
                userResponse.setCode(1);
            } else {

                UserEntity userEntity = userOptional.get();
                RoleEntity userRole = userEntity.getRole();
                UserDataResponseDTO userDataResponseDTO = UserMapper.toResponseDTO(userEntity);

                if (userRole != null) {
                    userDataResponseDTO.setRoleDescription(userRole.getDescription());
                    userDataResponseDTO.setRolePermission(userRole.getPermission());
                }

                userResponse.setMessage("Usuario encontrado");
                userResponse.setData(userDataResponseDTO);
                userResponse.setCode(2);
            }

        } catch (DataAccessException ex) {
            userResponse.setMessage(ex.getMessage());
            userResponse.setCode(3);
        }
        return userResponse;
    }


    //-------------------
    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequest) {

        UserResponseDTO userResponse = new UserResponseDTO();
        try {
            Optional<UserEntity> existingUser = userRepository.findUserByEmail(userRequest.getEmail());
            response = new HashMap<>();

            // Codificar el password
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            String hash = argon2.hash(1, 1024, 1, userRequest.getPassword());
            userRequest.setPassword(hash);



            if (existingUser.isPresent()) {
                userResponse.setMessage("El email ya esta registrado");
                userResponse.setCode(1);
            } else {

                UserEntity newUser = UserMapper.toEntity(userRequest);

                // Asigna role por defecto = 1 (user)
                RoleEntity defaultRole = roleRepository.findById(1).orElse(null);
                newUser.setRole(defaultRole);
                newUser.setState("activo");

                userRepository.save(newUser);

                RoleEntity userRole = newUser.getRole();
                UserDataResponseDTO newUserResponse = UserMapper.toResponseDTO(newUser);
                newUserResponse.setRoleDescription(userRole.getDescription());
                newUserResponse.setRolePermission(userRole.getPermission());

                userResponse.setMessage("Usuario creado con existo");
                userResponse.setData(newUserResponse);
                userResponse.setCode(2);

            }

        } catch (DataAccessException ex) {
            userResponse.setMessage(ex.getMessage());
            userResponse.setCode(3);
        }

        return userResponse;
    }

    //-------------------
    @Override
    public UserResponseDTO updateUser(UserRequestDTO userRequest) {
        UserResponseDTO userResponse = new UserResponseDTO();
        try {
            Optional<UserEntity> existingUser = userRepository.findUserById(userRequest.getId());

            if (!existingUser.isPresent()) {
                userResponse.setMessage("Usuario no existe.");
                userResponse.setCode(0);
            } else {
                UserEntity userToUpdate = existingUser.get();

                // Verifica si el nuevo correo ya existe en otro usuario
                if (userRequest.getEmail() != null && !userRequest.getEmail().equals(userToUpdate.getEmail())) {
                    Optional<UserEntity> userWithNewEmail = userRepository.findUserByEmail(userRequest.getEmail());
                    if (userWithNewEmail.isPresent()) {
                        userResponse.setMessage("El correo ya existe en otro usuario.");
                        userResponse.setCode(1);
                    } else {
                        userToUpdate.setEmail(userRequest.getEmail());
                    }
                }

                if (userRequest.getName() != null) {
                    userToUpdate.setName(userRequest.getName());
                }
                if (userRequest.getPassword() != null) {
                    Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
                    String hash = argon2.hash(1, 1024, 1, userRequest.getPassword());
                    userToUpdate.setPassword(hash);

                }
                if (userRequest.getState() != null) {
                    userToUpdate.setState(userRequest.getState());
                }
                if (userRequest.getRole_id() != null) {
                    userToUpdate.setRole(roleRepository.findById(userRequest.getRole_id()).orElse(null));
                }
                userRepository.save(userToUpdate);

                RoleEntity userRole = userToUpdate.getRole();
                UserDataResponseDTO UserUpdateResponse = UserMapper.toResponseDTO(userToUpdate);
                UserUpdateResponse.setRoleDescription(userRole.getDescription());
                UserUpdateResponse.setRolePermission(userRole.getPermission());

                userResponse.setMessage("Usuario actualizado con éxito");
                userResponse.setData(UserUpdateResponse);
                userResponse.setCode(2);
            }
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            userResponse.setMessage(ex.getMessage());
            userResponse.setCode(3);
        }

        return userResponse;
    }



    //-------------------
    @Override
    public UserResponseDTO deleteUser(Long UserId) {
            UserResponseDTO userResponse = new UserResponseDTO();
        try {
            boolean exist = this.userRepository.existsById(UserId);

            if(!exist) {
                userResponse.setMessage("No existe el usuario");
                userResponse.setCode(1);
            }

            userRepository.deleteById(UserId);
            userResponse.setMessage("Usuario eliminado exitosamente");
            userResponse.setCode(2);
        } catch (DataAccessException ex) {
            userResponse.setMessage(ex.getMessage());
            userResponse.setCode(3);
        }

        return userResponse;
    }

}
