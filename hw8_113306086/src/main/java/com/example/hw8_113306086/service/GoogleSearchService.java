package com.example.hw8_113306086.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleSearchService {

    // 從 application.properties 讀取設定
    @Value("${google.cse.enabled}")
    private boolean enabled;

    @Value("${google.cse.apiKey}")
    private String apiKey;

    @Value("${google.cse.cx}")
    private String cx;

    // 注入 WebConfig 中建立的 RestTemplate
    @Autowired
    private RestTemplate restTemplate;

    public Map<String, String> search(String query) {
        Map<String, String> results = new HashMap<>();
        
        // 如果 application.properties 中關閉了此功能，就返回空結果
        if (!enabled) {
            System.out.println("Google CSE is disabled.");
            return results;
        }

        try {
            // 對查詢關鍵字進行 URL 編碼
            String q = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
            //測試中文是否可用
            System.out.println("Encoded query: " + q);

            
            // 發送 GET 請求並獲取回應

            // 組合 API 請求 URL
            URI uri = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/customsearch/v1")
                .queryParam("q", q)
                .queryParam("key", apiKey)
                .queryParam("cx", cx)
                .build(true) // **重要！保留 UTF-8 編碼**
                .toUri();
            //
            ResponseEntity<Map<String, Object>> resp = restTemplate.getForEntity(uri, (Class<Map<String, Object>>) (Class<?>) Map.class);
            Map<String, Object> body = resp.getBody();
            
           
            if (body == null) {
                System.out.println("Google API response body is null.");
                return results;
            }

            Object itemsObj = body.get("items");
            if (itemsObj instanceof List) {
                List<?> items = (List<?>) itemsObj;
                for (Object itemObj : items) {
                    if (itemObj instanceof Map) {
                        Map<String, Object> item = (Map<String, Object>) itemObj;
                        Object titleObj = item.get("title");
                        Object linkObj = item.get("link");
                        
                        String title = (titleObj != null) ? titleObj.toString() : "No Title";
                        String link = (linkObj != null) ? linkObj.toString() : "#";

                        if (!title.isEmpty() && !link.isEmpty()) {
                            results.put(title, link);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 處理 API 呼叫失敗或 JSON 解析失敗
            System.err.println("Error calling Google Search API: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }
}