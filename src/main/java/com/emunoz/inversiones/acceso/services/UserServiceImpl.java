package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.entity.RoleEntity;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.repositry.RoleRepository;
import com.emunoz.inversiones.acceso.repositry.UserRepository;
import com.emunoz.inversiones.acceso.userMapper.UserMapper;
import com.emunoz.inversiones.acceso.util.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
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
public class UserServiceImpl implements UserService {

    HashMap<String, Object> response;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserServiceImpl(UserRepository productRepository) {
        this.userRepository = userRepository;
    }

    //-------------------
    @Override
    public ResponseEntity<Object> getUsers() {
        response = new HashMap<>();
        try {
            List<UserEntity> userEntitiesEntities = this.userRepository.findAll();
            List<UserResponseDTO> userResponseDTOs = userEntitiesEntities.stream()
                    .map(userEntity -> {
                        UserResponseDTO dto = UserMapper.toResponseDTO(userEntity);

                        if (userEntity.getRole() != null) {
                            dto.setRoleDescription(userEntity.getRole().getDescription());
                            dto.setRolePermission(userEntity.getRole().getPermission());
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());

            if (userEntitiesEntities.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }


            response.put("message", "Usuarios encontrados");
            response.put("data", userResponseDTOs);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException ex) {
            response.put("message", "Error de conexión a la base de datos: " + ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //-------------------
    @Override
    public ResponseEntity<Object> getUserById(Long id) {
        response = new HashMap<>();
        Optional<UserEntity> userOptional = userRepository.findUserById(id);

        if (!userOptional.isPresent()) {
            response.put("error", true);
            response.put("message", "No existe el usuario");
            return new ResponseEntity<>(
                    response,
                    HttpStatus.CONFLICT
            );
        }

        UserEntity userEntity = userOptional.get();
        RoleEntity userRole = userEntity.getRole();
        UserResponseDTO userResponseDTO = UserMapper.toResponseDTO(userEntity);

        if (userRole != null) {
            userResponseDTO.setRoleDescription(userRole.getDescription());
            userResponseDTO.setRolePermission(userRole.getPermission());
        }

        response.put("data", userResponseDTO);
        response.put("message", "Usuario encontrado");
        return new ResponseEntity<>(
                response,
                HttpStatus.ACCEPTED
        );
    }


    //-------------------
    @Override
    public ResponseEntity<Object> createUser(UserRequestDTO userRequest) {

        Optional<UserEntity> existingUser = userRepository.findUserByEmail(userRequest.getEmail());
        response = new HashMap<>();

        // Codificar el password
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hash = argon2.hash(1, 1024, 1, userRequest.getPassword());
        userRequest.setPassword(hash);



        if (existingUser.isPresent()) {
            return new ResponseEntity<>("El email ya esta registrado", HttpStatus.CONFLICT);
        } else {
            try {
                UserEntity newUser = UserMapper.toEntity(userRequest);

                // Asigna role por defecto = 1 (user)
                RoleEntity defaultRole = roleRepository.findById(1).orElse(null);
                newUser.setRole(defaultRole);

                userRepository.save(newUser);

                RoleEntity userRole = newUser.getRole();
                UserResponseDTO newUserResponse = UserMapper.toResponseDTO(newUser);
                newUserResponse.setRoleDescription(userRole.getDescription());
                newUserResponse.setRolePermission(userRole.getPermission());

                response.put("message", "Usuario creado con existo");
                response.put("data", newUserResponse);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } catch (DataIntegrityViolationException ex) {

                response.put("message", "Error al crear el nuevo usuarios");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    //-------------------
    @Override
    public ResponseEntity<Object> updateUser(UserRequestDTO userRequest) {
        response = new HashMap<>();
        Optional<UserEntity> existingUser = userRepository.findUserById(userRequest.getId());

        // Producto no encontrado
        if (!existingUser.isPresent()) {
            response.put("message", "Usuario no existe.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        UserEntity userToUpdate = existingUser.get();

        // Verifica si el nuevo correo ya existe en otro usuario
        if (userRequest.getEmail() != null && !userRequest.getEmail().equals(userToUpdate.getEmail())) {
            Optional<UserEntity> userWithNewEmail = userRepository.findUserByEmail(userRequest.getEmail());
            if (userWithNewEmail.isPresent()) {
                response.put("message", "El correo ya existe en otro usuario.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            userToUpdate.setEmail(userRequest.getEmail());
        }

        if (userRequest.getName() != null) {
            userToUpdate.setName(userRequest.getName());
        }

        if (userRequest.getPassword() != null) {
            userToUpdate.setPassword(userRequest.getPassword());
        }
        if (userRequest.getState() != null) {
            userToUpdate.setState(userRequest.getState());
        }
        if (userRequest.getRole_id() != null) {
            userToUpdate.setRole(roleRepository.findById(userRequest.getRole_id()).orElse(null));
        }

        userRepository.save(userToUpdate);

        RoleEntity userRole = userToUpdate.getRole();
        UserResponseDTO UserUpdateResponse = UserMapper.toResponseDTO(userToUpdate);
        UserUpdateResponse.setRoleDescription(userRole.getDescription());
        UserUpdateResponse.setRolePermission(userRole.getPermission());

        response.put("message", "Usuario actualizado con éxito");
        response.put("data", UserUpdateResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //-------------------
    @Override
    public ResponseEntity<Object> deleteUser(Long UserId, String token) {
        response = new HashMap<>();
        boolean exist = this.userRepository.existsById(UserId);

        if(!exist) {
            response.put("error", true);
            response.put("message","No existe el usuario");
            return new ResponseEntity<>(
                    response,
                    HttpStatus.CONFLICT
            );
        }


        if (jwtUtil.getPermission(token) !=  2) { // Verifica si el usuario tiene permiso 2 (o el permiso requerido)
            response.put("error", true);
            response.put("message", "Permiso insuficiente para eliminar usuarios.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        userRepository.deleteById(UserId);
        response.put("message","Usuario eliminado exitosamente");
        return new ResponseEntity<>(
                response,
                HttpStatus.ACCEPTED

        );
    }

}
