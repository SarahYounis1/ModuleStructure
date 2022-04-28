package com.example.controller;
import com.example.domain.aggregate.User;
import com.example.security.models.AuthenticationRequest;
import com.example.security.models.AuthenticationResponse;
import com.example.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
//@RequestMapping("/api")
public class UserRestController {

    private UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);

    @Autowired
    public UserRestController(UserService userServiceImp) {
        this.userService = userServiceImp;
    }


    @GetMapping("/user")
    public User returnUser()  {
        LOGGER.info("A get user request initialized ");
        return userService.getByID();
    }

    //Adding Post Mapping to add new user
    @PostMapping("/register")
    public User createNewUser(@RequestBody User newUser)  {
        LOGGER.info("A create user request initialized ");
        LOGGER.trace("Creating new user ");
        newUser.setId(0L);
       return userService.save(newUser);
        //return newUser;
    }

    //add mapping for login authentication
    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
            throws BadCredentialsException {
        return  userService.createAuthenticationToken(authenticationRequest);
    }

    @PutMapping("/user")
    public User editUser(@RequestBody User editUser )  {
        LOGGER.info("A update user request initialized ");
        LOGGER.trace("updating user information " );
        return userService.update(editUser);
    }
    @DeleteMapping("/user")
    public void deleteUser() throws IOException {
        userService.deleteByID();
    }

    @PostMapping("/user/logout")
    public String logOut(HttpServletRequest request){
        userService.logOut(request);
        return "You're logged out";
    }

    @PostMapping("/user/logoutAll")
    public String logOutAll(){
        userService.logOutAll();
        return "You're logged out from all devices";
    }
}
