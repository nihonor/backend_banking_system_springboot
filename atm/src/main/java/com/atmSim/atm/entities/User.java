package com.atmSim.atm.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "atm")
@EntityListeners(AuditEntityListener.class)
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "role", nullable = false)
    private String role; // e.g., "USER", "ADMIN"

    @Column(name = "status", nullable = false)
    @ColumnDefault("'ACTIVE'")
    private String status; // "ACTIVE" or "SUSPENDED"

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;
}