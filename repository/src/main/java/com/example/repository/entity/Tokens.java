package com.example.repository.entity;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@RequiredArgsConstructor
@EqualsAndHashCode
@Table(name = "tokens")
public class Tokens {
    @Id
    @Column(name = "jwt_token")
    private String jwtToken;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Tokens{" +"username"+user.getUsername()+'\''+ "jwtToken='" + jwtToken + '\'' + '}';
    }
}
