package com.fitness.activityservice.controller;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.ActivityType;
import com.fitness.activityservice.service.ActivityService;
import org.apache.http.protocol.HTTP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ActivityControlllerTest {

    @Mock
    ActivityService activityService;

    @InjectMocks
    ActivityControlller activityControlller;


    ActivityRequest getActivityRequest(){
        ActivityRequest request = new ActivityRequest();
        request.setUserId("1");
        request.setType(ActivityType.RUNNING);
        request.setCaloriesBurned(100);
        return request;
    }

    ActivityResponse getActivityResponse(){
        ActivityResponse response = new ActivityResponse();
        response.setUserId("1");
        response.setType(ActivityType.RUNNING);
        response.setCaloriesBurned(100);
        return response;
    }

    @Test
    void addActivity() {
        ActivityRequest request = getActivityRequest();
        ActivityResponse activityResponse = getActivityResponse();
        Mockito.when(activityService.addActivity(request)).thenReturn(activityResponse);

        ResponseEntity<ActivityResponse> response = activityControlller.addActivity(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activityResponse, response.getBody());
    }

    @Test
    void getAllActivities() {
        List<ActivityResponse> activities = List.of(getActivityResponse());
        Mockito.when(activityService.getAllActivity("1")).thenReturn(activities);
        ResponseEntity<List<ActivityResponse>> response = activityControlller.getAllActivities("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activities, response.getBody());
    }

    @Test
    void getActivity() {
        ActivityResponse activityResponse = getActivityResponse();
        activityResponse.setId("123");
        Mockito.when(activityService.getActivity("123")).thenReturn(activityResponse);
        ResponseEntity<ActivityResponse> response = activityControlller.getActivity("123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activityResponse, response.getBody());
    }
}