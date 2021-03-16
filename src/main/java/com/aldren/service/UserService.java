package com.aldren.service;

import com.aldren.entity.User;
import com.aldren.exception.BadRequestException;
import com.aldren.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private UserRepository userRepository;

    public UserService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(User user) throws BadRequestException {
        log.info(String.format("Saving user %s", user.getId()));

        Optional<User> existingUser = userRepository.findById(user.getId());

        if(existingUser.isPresent()) {
            throw new BadRequestException(String.format("User with ID of %s already exists. Please use a different id.", user.getId()));
        }

        userRepository.save(user);
    }

}
