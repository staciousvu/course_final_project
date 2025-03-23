package com.example.courseapplicationproject.schedule;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ClearData {
    private final StringRedisTemplate redisTemplate;
    @EventListener(ApplicationReadyEvent.class)
    public void clearRedisAndSyncData() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll();
        System.out.println("✅ Redis data cleared on application startup!");

    }
    private final RestClient restClient;

    @EventListener(ApplicationReadyEvent.class)
    public void deleteAllIndices() {
        try {
            Request request = new Request("DELETE", "/_all");
            Response response = restClient.performRequest(request);
            System.out.println("✅ All Elasticsearch indices deleted successfully!");
        } catch (IOException e) {
            System.err.println("⚠ Failed to delete indices: " + e.getMessage());
        }
    }
}
