package com.example.repository.adapter;
import com.example.domain.aggregate.User;
import com.example.domain.repository.IUserRepository;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceAdapter implements IUserRepository {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtTokenUtil;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    public UserServiceAdapter(UserDetailsServiceImpl userDetailsService, JwtUtil jwtTokenUtil, TaskRepository taskRepository,
                              UserRepository theUserRepository, TokenRepository tokenRepository,
                              AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.taskRepository = taskRepository;
        this.userRepository = theUserRepository;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
    }


    //sec version
    public UserEntity createNewUser(UserEntity newUser)  {
        if(userRepository.findByUsername(newUser.getUsername()).isEmpty()){
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            newUser.setPassword( "{bcrypt}" + encoder.encode(newUser.getPassword()));
            userRepository.save(newUser);
            return newUser;
        }
        else {

            throw new UserAlreadyExistException();
        }
    }

    //ses version
    @Transactional
    public UserEntity getUserInfo(){

        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public AuthenticationResponse createAuthenticationToken(AuthenticationRequest authenticationRequest) {
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {

            throw new BadCredentialsException("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
       Tokens token = new Tokens();
        UserEntity user=(UserEntity)userDetails;
        token.setUser(user);
        token.setJwtToken(jwt);
        tokenRepository.save(token);
        user.addToken(token);
        userRepository.save(user);
        return new AuthenticationResponse(jwt);
    }

    @Transactional
    public UserEntity editUser(UserEntity editUser) {
        UserEntity requestingUser= (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        requestingUser.setPassword( "{bcrypt}" + encoder.encode(editUser.getPassword()));
        requestingUser.setName(editUser.getName());
        requestingUser.setEmail(editUser.getEmail());
        requestingUser.setAge(editUser.getAge());
        userRepository.save(requestingUser);
        return requestingUser;
    }

    @Transactional
    public void deleteUser() {
        UserEntity requestingUser= (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        taskRepository.deleteAllByUser_Id(requestingUser.getId());
        tokenRepository.deleteAllByUserId(requestingUser.getId());
        userRepository.deleteById(requestingUser.getId());
    }

//    public void logOut(HttpServletRequest request) {
//        final String authorizationHeader = request.getHeader("Authorization");
//        String jwt = authorizationHeader.substring(7); //get the jwt and delete by it
//        tokenRepository.deleteById(jwt);
//    }
//
//    public void logOutAll() {
//        //delete all  jwt for this user
//        UserEntity requestingUser= (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        tokenRepository.deleteAllByUserId(requestingUser.getId());
//
//    }

    @Override
    public User save(User user) {
        UserEntity userE = convertToEntity(user);
        return convertToModel(createNewUser(userE));
    }

    @Override
    public User update(User user) {
        UserEntity userE = convertToEntity(user);
        return convertToModel(editUser(userE));
    }

    @Override
    public void deleteByID() {
            deleteUser();
    }

    @Override
    public User getByID() {
        return convertToModel(getUserInfo());
    }

    private UserEntity convertToEntity(User user){
        return modelMapper.map(user, UserEntity.class);
    }

    private User convertToModel(UserEntity userEntity){
        return modelMapper.map(userEntity, User.class);
    }
}