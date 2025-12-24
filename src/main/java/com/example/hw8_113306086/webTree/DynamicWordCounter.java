package com.example.hw8_113306086.webTree;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * DynamicWordCounter
 * 用途：替代原本的 WordCounter，改用 Selenium 抓取動態網頁 (SPA/CSR) 的內容。
 */
public class DynamicWordCounter {
    private String urlStr;
    private String content; // 暫存抓到的文字內容，避免同一個網站重複開瀏覽器

    public DynamicWordCounter(String urlStr) {
        this.urlStr = urlStr;
    }

    /**
     * 核心方法：啟動 Selenium Browser 抓取文字
     */
    private String fetchContent() {
        // 1. 過濾黑名單網站 (與原本邏輯一致)
        /*if (urlStr.contains("reddit.com") ||
            urlStr.contains("facebook.com") ||
            urlStr.contains("threads.com") ||
            urlStr.contains("instagram.com") ||
            urlStr.contains("twitter.com")) {
            return "";
        }*/

        System.out.println("正在透過 Selenium 抓取: " + this.urlStr);

        // 2. 設定 Chrome Options (記憶體優化與隱藏視窗)
        //WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        // 基礎設定：無頭模式 (不跳出視窗)
        options.addArguments("--headless=new"); 
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080"); // 模擬正常螢幕大小，避免 RWD 隱藏內容
        options.addArguments("--disable-dev-shm-usage"); // 防止在某些環境下崩潰

        // 進階優化：禁止載入圖片 (大幅節省 RAM 和流量)
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2); // 2 = 禁止
        options.setExperimentalOption("prefs", prefs);

        // 載入策略：Eager (只要 DOM 載入完就好，不用等廣告/外部資源跑完)
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
            
            // 設定等待超時時間
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

            // 前往網址
            driver.get(this.urlStr);

            // 稍微等待 JS 渲染 (暴力但有效)
            // 如果網站很慢，可以考慮把這裡加大，或者改用 WebDriverWait
            Thread.sleep(1500); 

            // 抓取 <body> 內的純文字
            String text = driver.findElement(By.tagName("body")).getText();
            
            if (text == null) return "";
            return text;

        } catch (Exception e) {
            System.err.println("Selenium error processing " + this.urlStr + ": " + e.getMessage());
            return "";
        } finally {
            // *** 非常重要：務必關閉瀏覽器，否則記憶體會爆掉 ***
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * 計算關鍵字出現次數
     * 邏輯與原本 WordCounter 完全一樣，只差在 content 來源不同
     */
    public int countKeyword(String keyword) {
        if (content == null) {
            content = fetchContent();
        }

        // 如果抓回來是空的，直接回傳 0
        if (content.isEmpty()) {
            return 0;
        }

        // 簡單的字串搜尋演算法
        // 若需要忽略大小寫，可在此處做 toUpperCase()
        content = content.toUpperCase();
        keyword = keyword.toUpperCase();

        int retVal = 0;
        int fromIdx = 0;
        int found = -1;

        while ((found = content.indexOf(keyword, fromIdx)) != -1) {
            retVal++;
            fromIdx = found + keyword.length();
        }

        return retVal;
    }
    public String getContent() {
        return this.content;
    }
}