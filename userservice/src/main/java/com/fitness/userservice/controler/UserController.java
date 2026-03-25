package com.fitness.userservice.controler;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserReponse;
import com.fitness.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/users"})
@AllArgsConstructor // Used for dependency injection
public class UserController {

    private UserService userService;
    @GetMapping("/{userId}")
    public ResponseEntity<UserReponse> getUserDetails(@PathVariable String userId){
        return ResponseEntity.ok(userService.getUserDetails(userId));
    }


    //@Valid - Used to perform validation on the request object
    @PostMapping("/register")
    public ResponseEntity<UserReponse> registerUser(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId){
        return ResponseEntity.ok(userService.existByUserId((userId)));
    }



}
