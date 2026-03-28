package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity){

        log.info("Activity received from RabbitMQ" + activity);
        String recommendation = activityAIService.generateRecommendation(activity);
        log.info("Recommendation generated from AI:" + recommendation);

    }

}
