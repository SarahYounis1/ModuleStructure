package com.example.domain.service;


import com.example.domain.aggregate.User;
import com.example.security.models.AuthenticationRequest;
import com.example.security.models.AuthenticationResponse;

public interface IUserService {

    User save(User user);
    User update(User user);
    void deleteByID();
    User getByID();
    AuthenticationResponse createAuthenticationToken(AuthenticationRequest authenticationRequest) ;
}
