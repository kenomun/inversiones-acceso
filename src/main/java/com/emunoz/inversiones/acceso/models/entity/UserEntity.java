package com.emunoz.inversiones.acceso.models.entity;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

}
