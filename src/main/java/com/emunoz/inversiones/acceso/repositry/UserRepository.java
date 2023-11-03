package com.emunoz.inversiones.acceso.repositry;

import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

        Optional<UserEntity> findUserByEmail(String email);
        Optional<UserEntity> findUserById(Long id);
        Optional<UserEntity> findByEmail(String email);



}
