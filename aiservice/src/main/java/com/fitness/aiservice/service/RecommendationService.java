package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RecommendationService {
        private final RecommendationRepository recommendationRepository;

        public List<Recommendation> getUserRecommendation(String userId) {
                 return recommendationRepository.findAllByUserId(userId);
        }

        public Recommendation getActivityRecommendation(String activityId) {
                return recommendationRepository.findByActivityId(activityId)
                        .orElseThrow(() -> new RuntimeException("Activity does not exist with activity id: " + activityId));

        }
}
