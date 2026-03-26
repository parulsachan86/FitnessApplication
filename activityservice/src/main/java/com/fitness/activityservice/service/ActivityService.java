package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ActivityService {

    private ActivityRepository activityRepository;
    private ValidateUserService validateUserService;

    public ActivityResponse addActivity( ActivityRequest request) {
        Boolean isValidUser = validateUserService.validateUser(request.getUserId());
        if(!isValidUser){
            throw new RuntimeException("User does not with user_id" + request.getUserId());
        }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type((request.getType()))
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .additionalMetrics(request.getAdditionalMetrics())
                .startTime(request.getStartTime())
                .build();

        Activity savedActivity = activityRepository.save(activity);
        return mapToResponse(savedActivity);
    }

    public ActivityResponse mapToResponse(Activity activity){
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setStartTime(activity.getStartTime());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        return response;
    }

    public List<ActivityResponse> getAllActivity(String userID) {
        List<Activity> activities = activityRepository.findByUserId(userID);
        return activities.stream()
                .map(this:: mapToResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivity(String activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not present in the database with id: " + activityId));
        return mapToResponse((activity));
    }
}
