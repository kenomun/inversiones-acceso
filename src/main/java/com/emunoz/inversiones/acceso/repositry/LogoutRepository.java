package com.emunoz.inversiones.acceso.repositry;

import com.emunoz.inversiones.acceso.models.entity.RevokedTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogoutRepository extends JpaRepository<RevokedTokenEntity, Long> {
    Optional<RevokedTokenEntity> findByToken(String token);
}
