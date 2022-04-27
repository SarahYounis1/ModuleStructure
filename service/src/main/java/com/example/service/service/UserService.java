package com.example.service.service;

import com.example.domain.aggregate.User;;
import com.example.domain.repository.IUserRepository;
import com.example.domain.service.IUserService;
import com.example.repository.entity.UserEntity;
import com.example.repository.repository.TokenRepository;
import com.example.security.models.AuthenticationRequest;
import com.example.security.models.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
@Service
public class UserService implements IUserService {

    private final TokenRepository tokenRepository;
    @Autowired
    private IUserRepository userRepository;

    public UserService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void logOut(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = authorizationHeader.substring(7); //get the jwt and delete by it
        tokenRepository.deleteById(jwt);
    }

    public void logOutAll() {
        //delete all  jwt for this user
        UserEntity requestingUser= (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tokenRepository.deleteAllByUserId(requestingUser.getId());

    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.update(user);
    }

    @Override
    public void deleteByID() {
     userRepository.deleteByID();
    }

    @Override
    public User getByID() {
       return userRepository.getByID();
    }

    @Override
    public AuthenticationResponse createAuthenticationToken(AuthenticationRequest authenticationRequest) {
        return userRepository.createAuthenticationToken(authenticationRequest);
    }
}
