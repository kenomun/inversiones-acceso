package com.emunoz.inversiones.acceso.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {

    @Id
    private Integer id;
    private String description;
    private Integer permission;

}
