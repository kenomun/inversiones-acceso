package com.emunoz.inversiones.acceso.models.response;

import lombok.Data;

@Data
public class UserDataResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String state;
    private String roleDescription;
    private Integer rolePermission;

    public UserDataResponseDTO() {
    }

    public UserDataResponseDTO(Long id, String name, String email, String password, String state) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.state = state;
    }

    public void setRoleDescription(String description) {
        this.roleDescription = description;
    }

    public void setRolePermission(Integer permission) {
        this.rolePermission = permission;
    }
}
