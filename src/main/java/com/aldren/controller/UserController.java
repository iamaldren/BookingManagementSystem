package com.aldren.controller;

import com.aldren.entity.User;
import com.aldren.exception.BadRequestException;
import com.aldren.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/users")
    public void saveUser(@RequestBody User user) throws BadRequestException {
        userService.saveUser(user);
    }

}
