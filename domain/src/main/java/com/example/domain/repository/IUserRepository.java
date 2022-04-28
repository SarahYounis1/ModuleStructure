package com.example.domain.repository;


import com.example.domain.aggregate.User;
import com.example.repository.entity.UserEntity;
import com.example.security.models.AuthenticationRequest;
import com.example.security.models.AuthenticationResponse;

import java.util.Optional;

public interface IUserRepository {
    AuthenticationResponse createAuthenticationToken(AuthenticationRequest authenticationRequest);

    User save(User user);
    User update(User user);
    void deleteByID();
    User getByID();
}
