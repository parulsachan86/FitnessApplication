package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserReponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserReponse register(RegisterRequest request) {

        if(repository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email Already exist");
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        User savedUser = repository.save(newUser);

        UserReponse userReponse = new UserReponse();
        userReponse.setEmail(savedUser.getEmail());
        userReponse.setPassword(savedUser.getPassword());
        userReponse.setFirstName((savedUser.getFirstName()));
        userReponse.setLastName(savedUser.getLastName());
        userReponse.setCreatedAt(savedUser.getCreatedAt());
        userReponse.setUpdateAt(savedUser.getUpdateAt());
        return userReponse;
    }

    public UserReponse getUserDetails(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException(" User does not exist with userId:" + userId));

        UserReponse userReponse = new UserReponse();
        userReponse.setEmail(user.getEmail());
        userReponse.setPassword(user.getPassword());
        userReponse.setFirstName((user.getFirstName()));
        userReponse.setLastName(user.getLastName());
        userReponse.setCreatedAt(user.getCreatedAt());
        userReponse.setUpdateAt(user.getUpdateAt());
        return userReponse;
    }

    public Boolean existByUserId(String userId) {
        return repository.existsById(userId);
    }
}
