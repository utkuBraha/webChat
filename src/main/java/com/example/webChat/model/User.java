package com.example.webChat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "userDb")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable=false)
    private String password;

    @Column(nullable = false)
    private String username;

    @JsonIgnore
    public Long getId() {
        return id;
    }
    @JsonIgnore
    public void setRoles(Set<String> user) {
    }
    @JsonIgnore
    public Object getRoles() {
        return null;
    }
}
