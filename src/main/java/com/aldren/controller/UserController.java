package com.aldren.controller;

import com.aldren.entity.User;
import com.aldren.exception.BadRequestException;
import com.aldren.exception.DefaultInternalServerException;
import com.aldren.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class UserController {

    private UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/users")
    public void saveUser(@RequestBody User user) throws BadRequestException, DefaultInternalServerException {
        try {
            userService.saveUser(user);
        } catch (BadRequestException e) {
            log.warn(String.format(e.getLocalizedMessage()));
            throw e;
        } catch (RedisConnectionFailureException e) {
            log.error(String.format("Error creating user %s", user.getId()), e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        } catch (Exception e) {
            log.error(String.format("Error creating user %s", user.getId()), e);
        }
    }

}
