package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.model.ActivityType;
import com.fitness.activityservice.repository.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {
    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ValidateUserService validateUserService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ActivityService activityService;

    public ActivityRequest getActivityRequest(ActivityType type,int calories) {
        ActivityRequest request = new ActivityRequest();
        request.setUserId("1");
        request.setType(type);
        request.setCaloriesBurned(calories);
        return request;
    }

    public Activity getActivity(ActivityType type, int calories) {
        Activity activity = new Activity();
        activity.setUserId("1");
        activity.setType(type);
        activity.setCaloriesBurned(calories);
        return activity;
    }

    @Test
    void addActivity() {
        ActivityRequest activityRequest1 = getActivityRequest(ActivityType.RUNNING, 100);
        Activity activity1 = getActivity(ActivityType.RUNNING, 100);
        when(validateUserService.validateUser("1")).thenReturn(true);
        when(activityRepository.save(any(Activity.class))).thenReturn(activity1);

        ActivityResponse response = activityService.addActivity(activityRequest1);
        assertEquals("1", response.getUserId());
        assertEquals(ActivityType.RUNNING, response.getType());
        assertEquals(100, response.getCaloriesBurned());
    }

    @Test
    void addActivity_InvalidUser() {
        ActivityRequest activityRequest1 = getActivityRequest(ActivityType.RUNNING, 100);
        when(validateUserService.validateUser("1")).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> activityService.addActivity(activityRequest1));
        assertEquals("User does not exist with user_id: 1", exception.getMessage());
    }

    @Test
    void addActivity_RabbitMq() {
        ActivityRequest activityRequest1 = getActivityRequest(ActivityType.RUNNING, 100);
        Activity activity1 = getActivity(ActivityType.RUNNING, 100);
        when(validateUserService.validateUser("1")).thenReturn(true);
        when(activityRepository.save(any(Activity.class))).thenReturn(activity1);
        doThrow(new AmqpException("RabbitMQ is not available")).when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Activity.class));

        // The method should not throw an exception since RabbitMQ exception is caught
        ActivityResponse response = activityService.addActivity(activityRequest1);

        // Verify the response is still returned even if RabbitMQ fails
        assertNotNull(response);
        assertEquals("1", response.getUserId());
        assertEquals(ActivityType.RUNNING, response.getType());
        assertEquals(100, response.getCaloriesBurned());

        // Verify that the activity was saved
        verify(activityRepository, times(1)).save(any(Activity.class));

        // Verify that convertAndSend was attempted
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Activity.class));
    }

    @Test
    void getAllActivity_EmptyActivities() {
            when(activityRepository.findByUserId(anyString())).thenReturn(Collections.emptyList());
            List<ActivityResponse> response = activityService.getAllActivity("1");
            assertEquals(0, response.size());
    }

    @Test
    void getAllActivity(){
        List<Activity> activities = new ArrayList<>();
        activities.add(getActivity(ActivityType.RUNNING, 100));
        activities.add(getActivity(ActivityType.CYCLING, 200));
        when(activityRepository.findByUserId("1")).thenReturn(activities);
        List<ActivityResponse> responses = activityService.getAllActivity("1");
        assertEquals(2, responses.size());
        assertEquals(ActivityType.RUNNING, responses.get(0).getType());
        assertEquals(100, responses.get(0).getCaloriesBurned());
        assertEquals(ActivityType.CYCLING, responses.get(1).getType());
        assertEquals(200, responses.get(1).getCaloriesBurned());
    }

    @Test
    void getActivity() {
        Activity activity = getActivity(ActivityType.RUNNING, 100);
        activity.setId("123");
        when(activityRepository.findById("123")).thenReturn(Optional.of(activity));
        ActivityResponse response = activityService.getActivity("123");
        assertEquals("123", response.getId());
        assertEquals("1", response.getUserId());
    }

    @Test
    void getActivity_ActivityNotPresent(){
        when(activityRepository.findById("123")).thenReturn(Optional.empty());
        try{
            ActivityResponse response = activityService.getActivity("123");
            fail("RuntimeException expected");
        }
        catch (RuntimeException re){
            String expectedMessage = "Activity not present in the database with id: 123";
            assertEquals(expectedMessage, re.getMessage());
        }

    }
}