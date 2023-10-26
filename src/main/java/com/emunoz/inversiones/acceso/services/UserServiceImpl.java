package com.emunoz.inversiones.acceso.services;

import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import com.emunoz.inversiones.acceso.models.request.UserRequestDTO;
import com.emunoz.inversiones.acceso.models.response.UserResponseDTO;
import com.emunoz.inversiones.acceso.repositry.UserRepository;
import com.emunoz.inversiones.acceso.userMapper.UserMapper;
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
            List<UserResponseDTO> productResponseDTOs = userEntitiesEntities.stream()
                    .map(UserMapper::toResponseDTO)
                    .collect(Collectors.toList());

            if (userEntitiesEntities.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            response.put("message", "Usuarios encontrados");
            response.put("data", productResponseDTOs);
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
        UserResponseDTO userResponseDTO = UserMapper.toResponseDTO(userEntity);
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
        System.out.println("PASE POR AQUI");
        Optional<UserEntity> existingUser = userRepository.findUserByEmail(userRequest.getEmail());
        response = new HashMap<>();

        if (existingUser.isPresent()) {
            return new ResponseEntity<>("El email ya esta registrado", HttpStatus.CONFLICT);
        } else {
            try {
                UserEntity newUser = UserMapper.toEntity(userRequest);
                userRepository.save(newUser);
                UserResponseDTO newUserResponse = UserMapper.toResponseDTO(newUser);
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
        System.out.println("UPDATE USER");
        response = new HashMap<>();
        Optional<UserEntity> existingUser = userRepository.findUserById(userRequest.getId());
        System.out.println("request" + userRequest);
        System.out.println("usuario existe" + existingUser);

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
        userToUpdate.setRole_id(userRequest.getRole_id());
        }


        userRepository.save(userToUpdate);
        response.put("message", "Producto actualizado con éxito");
        response.put("data", UserMapper.toResponseDTO(userToUpdate));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //-------------------
    @Override
    public ResponseEntity<Object> deleteUser(Long UserId) {
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

        userRepository.deleteById(UserId);
        response.put("message","Usuario eliminado exitosamente");
        return new ResponseEntity<>(
                response,
                HttpStatus.ACCEPTED

        );
    }

}
