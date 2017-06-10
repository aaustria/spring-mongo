package com.event.controllers;

import com.event.entities.User;
import com.event.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
@RequestMapping("/api/user")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value="/register", method= RequestMethod.POST)
    ResponseEntity registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value="/{userName}", method= RequestMethod.GET)
    ResponseEntity<User> getUser(
            @PathVariable(value="userName") String userName
    ) {
        return new ResponseEntity(userService.getUserByUserName(userName), HttpStatus.OK);
    }

    // TODO: update user

    // TODO: delete user
}
