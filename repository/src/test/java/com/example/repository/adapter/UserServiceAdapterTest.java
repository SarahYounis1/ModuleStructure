package com.example.repository.adapter;

import com.example.domain.aggregate.User;

import com.example.exception.UserAlreadyExistException;
import com.example.repository.entity.Tokens;
import com.example.repository.entity.UserEntity;
import com.example.repository.repository.TaskRepository;
import com.example.repository.repository.TokenRepository;
import com.example.repository.repository.UserRepository;
import com.example.security.JWTSecurity.JwtUtil;
import com.example.security.UserDetailsServiceImpl;
import com.example.security.models.AuthenticationRequest;
import com.example.security.models.AuthenticationResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceAdapterTest {
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private JwtUtil jwtTokenUtil;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private UserServiceAdapter userServiceImplementation;
    private final ModelMapper modelMapper = new ModelMapper();

    private final User user = new User(1L,"Sarah","Sarahajam@gmail.com","sarahY","sarah123",21);

    @Test
    void savePass() throws Exception{
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.user.setPassword("{bcrypt}" + encoder.encode(this.user.getPassword()));
       // when(userRepository.save(this.user)).thenReturn(this.user);
        assertEquals(this.user.getId(),userServiceImplementation.save(this.user).getId());

    }
    @Test
    void saveFail(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.user.setPassword("{bcrypt}" + encoder.encode(this.user.getPassword()));
        when(userRepository.findByUsername(this.user.getUsername())).
                thenReturn(Optional.ofNullable(convertToEntity(this.user)));
        assertThrows(UserAlreadyExistException.class,
                ()-> userServiceImplementation.save(this.user));
    }


    private UserEntity convertToEntity(User user){
        return modelMapper.map(user, UserEntity.class);
    }


    @Test
    void getUserInfo() throws Exception{
        //we have to create security context holder
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(this.user);
        assertEquals(this.user,userServiceImplementation.getByID());
    }

    @Test
    void createAuthenticationTokenPass() throws Exception{
        UserEntity userE =convertToEntity(user);
        AuthenticationRequest authenticationRequest=
                new AuthenticationRequest("sarahY","sarah123");
        when(userDetailsService.loadUserByUsername
                (authenticationRequest.getUsername())).thenReturn(userE);
        String token= jwtTokenUtil.generateToken(userE);
        when(jwtTokenUtil.generateToken(any())).thenReturn(token);
        Tokens tokens = new Tokens();
        tokens.setUser(userE);
        tokens.setJwtToken(token);
     //   when(tokenRepository.save(tokens)).thenReturn(tokens);
        userE.addToken(tokens);
       when(userRepository.save(userE)).thenReturn(userE);
        AuthenticationResponse authenticationResponse =new AuthenticationResponse(token);
        assertEquals(authenticationResponse.getJwt(),
                userServiceImplementation.createAuthenticationToken(authenticationRequest).getJwt());
    }
    @Test
    void createAuthenticationTokenFail()throws Exception {
        AuthenticationRequest authenticationRequest=
                new AuthenticationRequest("sarahY","sarah123");
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                (authenticationRequest.getUsername(), authenticationRequest.getPassword())))
                .thenThrow(BadCredentialsException.class);
        assertThrows(BadCredentialsException.class,()->userServiceImplementation.createAuthenticationToken(authenticationRequest));

    }
    @Test
    void update() throws Exception{
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.user.setPassword( "{bcrypt}" + encoder.encode(this.user.getPassword()));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        lenient().when(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(this.user);
        lenient().when(userRepository.save(convertToEntity(this.user))).thenReturn(convertToEntity(this.user));
        assertEquals(this.user,userServiceImplementation.update(this.user));

    }


    @Test
    void deleteByID() throws IOException {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(this.user);
        userServiceImplementation.deleteByID();
        verify(taskRepository,times(1)).deleteAllByUser_Id(this.user.getId());
        verify(tokenRepository,times(1)).deleteAllByUserId(this.user.getId());
        verify(userRepository,times(1)).deleteById(this.user.getId());
    }



}
