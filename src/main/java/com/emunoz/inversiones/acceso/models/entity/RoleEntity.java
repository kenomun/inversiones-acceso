package com.emunoz.inversiones.acceso.models.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
@Data
public class RoleEntity {

    @Id
    private Integer id;
    private String description;
    private Integer permission;

    public RoleEntity() {

    }
    public RoleEntity(Integer id, String description, int permission) {
        this.id = id;
        this.description = description;
        this.permission = permission;
    }
}
