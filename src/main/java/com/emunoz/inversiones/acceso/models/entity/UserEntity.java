package com.emunoz.inversiones.acceso.models.entity;


import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private RoleEntity role;

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

    public UserEntity() {
    }

    public UserEntity(String name, String email, String password, String state) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.state = state;

    }

    public UserEntity(Long id, String name, String email, String password, String state) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.state = state;

    }
}
