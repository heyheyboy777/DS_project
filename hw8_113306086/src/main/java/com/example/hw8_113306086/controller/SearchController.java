package com.example.hw8_113306086.controller;


import com.example.hw8_113306086.service.GoogleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class SearchController {

    @Autowired
    private GoogleSearchService googleSearchService;

    // 處理根目錄 "/" 和 "/search" 請求
    @GetMapping("/")
    public String home() {
        // 只顯示 index.html (只有搜尋框)
        return "index";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(value = "query", required = false) String query,
            Model model
    ) {
        if (query != null && !query.isEmpty()) {
            // 1. 呼叫 Service 取得搜尋結果
            Map<String, String> searchResults = googleSearchService.search(query);
            
            // 2. 將結果放進 Model，這樣 HTML 頁面才能讀取
            model.addAttribute("results", searchResults);
            model.addAttribute("query", query); // 把查詢詞也傳回去，可以顯示在搜尋框
        }

        // 3. 返回 index.html 頁面 (這次會包含結果)
        return "index";
    }
}