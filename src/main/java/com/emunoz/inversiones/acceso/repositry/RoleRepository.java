package com.emunoz.inversiones.acceso.repositry;

import com.emunoz.inversiones.acceso.models.entity.RoleEntity;
import com.emunoz.inversiones.acceso.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
}
