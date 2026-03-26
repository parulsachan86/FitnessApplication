package com.fitness.activityservice.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Service
@AllArgsConstructor
public class ValidateUserService {

    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId){
        try{

            Boolean isValid = userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofSeconds(2))
                    .block();
            return isValid != null && isValid;
        }
        catch (WebClientResponseException e){
            switch (e.getStatusCode().value()){
                case 404:
                    System.out.println("UserId does not exist");
                    return false;
                case 500:
                    System.out.println("User Service internal error");
                    return false;
                default:
                    System.out.println("Received unexpected status" + e.getMessage());
            }
            throw new RuntimeException("Error occurred while communication with userService" + e.getMessage());
        }
    }


}
