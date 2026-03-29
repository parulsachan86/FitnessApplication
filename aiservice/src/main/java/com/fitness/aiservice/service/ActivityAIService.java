package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;
    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String aiRecommendation = geminiService.getRecommendationFromAI(prompt);
        // System.out.println(aiRecommendation);
        Recommendation recommendation =  parseAiRecommendation(aiRecommendation, activity);
        return recommendation;
    }

    public List<String> jsonArrayToList(JsonArray array) {
        List <String> list = new ArrayList<>();
        for (JsonElement element : array) {
            list.add(element.toString());
        }
        return list;
    }

    public Recommendation parseAiRecommendation (String aiRecommendation, Activity activity) {
        try{
            JsonObject jsonRecommendation = JsonParser.parseString(aiRecommendation).getAsJsonObject();
            JsonArray candidates = jsonRecommendation.get("candidates").getAsJsonArray();
            JsonObject candidate = candidates.get(0).getAsJsonObject();
            JsonObject content = candidate.get("content").getAsJsonObject();
            JsonArray parts = content.get("parts").getAsJsonArray();
            JsonObject part = parts.get(0).getAsJsonObject();
            String text = part.get("text").getAsString();

            String cleanText = text
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonObject finalAIRecommendation = JsonParser.parseString(cleanText).getAsJsonObject();
            System.out.println(finalAIRecommendation);

            Recommendation recommendation = new Recommendation();
            recommendation.setRecommendation(finalAIRecommendation.get("analysis").toString());

            JsonArray improvements = finalAIRecommendation.get("improvements").getAsJsonArray();
            recommendation.setImprovements(jsonArrayToList(improvements));


            JsonArray suggestions = finalAIRecommendation.get("suggestions").getAsJsonArray();
            recommendation.setSuggestions(jsonArrayToList(suggestions));

            JsonArray safety = finalAIRecommendation.get("safety").getAsJsonArray();
            recommendation.setSafety(jsonArrayToList(safety));

            recommendation.setActivityId(activity.getId());
            recommendation.setActivityType(activity.getType());
            recommendation.setUserId(activity.getUserId());
            recommendation.setCreatedAt(activity.getCreatedAt());
            return recommendation;
        }
        catch (Exception e){
            log.error("Error occurred in parsing the AIRecommendation response" + e.getMessage());
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
          return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
