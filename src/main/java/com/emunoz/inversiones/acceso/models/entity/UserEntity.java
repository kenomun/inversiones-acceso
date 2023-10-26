package com.emunoz.inversiones.acceso.models.entity;


import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "state")
    private String state;

    @Column(name = "role_id")
    private Integer role_id;

    public UserEntity() {
    }

    public UserEntity(String name, String email, String password, String state, Integer role_id) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.state = state;
        this.role_id = role_id;
    }

    public UserEntity(Long id, String name, String email, String password, String state, Integer role_id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.state = state;
        this.role_id = role_id;
    }
}
