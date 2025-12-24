package com.example.hw8_113306086.controller;
//限制多次執行
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;
import java.util.Arrays;

import com.example.hw8_113306086.crawler.Crawler;
import com.example.hw8_113306086.dto.SearchResultDTO;
import com.example.hw8_113306086.service.GoogleSearchService;
import com.example.hw8_113306086.webTree.FetchMode;
import com.example.hw8_113306086.webTree.Keyword;
import com.example.hw8_113306086.webTree.TreeBuilder;
import com.example.hw8_113306086.webTree.WebTree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;

@Controller
public class SearchController {
    // 在 SearchController 類別成員變數區
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
    // --- 中文停用詞 ---
    "的", "了", "和", "是", "就", "都", "而", "及", "與", "著", "或", "一", "在", "有", "不",
    "這", "那", "個", "之", "上", "下", "但", "去", "過", "對", "從", "向", "為", "把", "被",
    "讓", "很", "更", "人", "們", "於", "此", "其", "後", "前", "當", "時", "中", "種", "將",
    "到", "由", "得", "做", "該", "比", "並", "等", "日", "月", "年", "我", "你", "他", "她",
    "它", "們", "自己", "我們", "可以", "這個", "就是", "因為", "所以", "如果", "但是", "以及",
    "對於", "關於", "之類", "時候", "這樣", "那樣", "這些", "那些", "部分", "所有", "沒有",
    "相關", "結果", "搜尋", "網站", "內容", "新聞", "首頁", "圖片", "影片",
    // --- 英文停用詞 (English Stop Words) ---
    "the", "a", "an", "and", "or", "but", "is", "are", "was", "were", "to", "of", "in", 
    "on", "at", "by", "for", "with", "about", "as", "it", "this", "that", "these", "those", 
    "he", "she", "they", "we", "you", "i", "his", "her", "their", "my", "your", "be", 
    "have", "has", "do", "does", "not", "can", "will", "would", "should", "could"
    ));

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
            @RequestParam(value = "query", required = false) String query,@RequestParam(value = "mode", required = false, defaultValue = "dynamic") String mode,
            Model model
    ) {
        if (query != null && !query.isEmpty()) {
            // 1. 呼叫 Service 取得搜尋結果
            //先試一下幾個keyword
            ArrayList<Keyword> keywords = new ArrayList<Keyword>();
            keywords.add(new Keyword(query,7));
            keywords.add(new Keyword("判決",5.0));
            keywords.add(new Keyword("殺人",10.0));
            keywords.add(new Keyword("scandal",10.0));
             keywords.add(new Keyword("controversy",10.0));
              keywords.add(new Keyword(" argument",10.0));
               keywords.add(new Keyword("news",10.0));
                keywords.add(new Keyword("wanted",10.0));
               keywords.add(new Keyword("sue",10.0));
            keywords.add(new Keyword("arrest",10.0));
          
            keywords.add(new Keyword("綁架",10.0));
            keywords.add(new Keyword("爭議",10.0));
            keywords.add(new Keyword("被告",5.0));
            keywords.add(new Keyword("裁定",5.0));
            keywords.add(new Keyword("起訴",5.0));
            keywords.add(new Keyword("通緝",5.0));
            keywords.add(new Keyword("警",4.0));
            keywords.add(new Keyword("涉嫌",4.0));
            keywords.add(new Keyword("嫌",4.0));
            keywords.add(new Keyword("涉",4.0));
            keywords.add(new Keyword("嗆",10.0));
            keywords.add(new Keyword("逮補",4.0));
            keywords.add(new Keyword("遭逮",4.0));
            keywords.add(new Keyword("落網",4.0));
            keywords.add(new Keyword("羈押",4.0));
            keywords.add(new Keyword("移送",4.0));
            keywords.add(new Keyword("調查",4.0));
            keywords.add(new Keyword("徒刑",4.0));
            keywords.add(new Keyword("勒",4.0));
            keywords.add(new Keyword("恐嚇",4.0));
            keywords.add(new Keyword("騷擾",4.0));
           // keywords.add(new Keyword("地檢署",4.0));
            keywords.add(new Keyword("爭議",4.0));
            keywords.add(new Keyword("不法",4.0));
            keywords.add(new Keyword("行兇",4.0));
            keywords.add(new Keyword("歹徒",4.0));
            keywords.add(new Keyword("指控",4.0));
            keywords.add(new Keyword("犯",4.0));
            keywords.add(new Keyword("案",4.0));
            keywords.add(new Keyword("賠",2.0));
           // keywords.add(new Keyword("法院",2.0));
           // keywords.add(new Keyword("法",2.0));
            keywords.add(new Keyword("死",2.0));
            keywords.add(new Keyword("判",2.0));
            keywords.add(new Keyword("貪",2.0));
            keywords.add(new Keyword("告",2.0));
            //keywords.add(new Keyword("檢",2.0));
            keywords.add(new Keyword("警方",2.0));
           // keywords.add(new Keyword("社會",1.0));
           // keywords.add(new Keyword("權",1.0));
            //keywords.add(new Keyword("訟",1.0));
            keywords.add(new Keyword("恐",1.0));
            keywords.add(new Keyword("黑",1.0));
            String query1;
            if(containsChinese(query)){
             query1 = query+"醜聞";}
            else{
             query1 = query+" scandal controversy";
            }
             // 傳遞 mode 參數到前端
            boolean isStaticMode = "static".equalsIgnoreCase(mode);
            model.addAttribute("mode", mode); // 讓頁面可回填 radio
            Map<String, String> searchResults = googleSearchService.search(query1);
            Crawler crawler1 = new Crawler(searchResults);
            //建立網站樹
            FetchMode fetchMode = isStaticMode ? FetchMode.STATIC : FetchMode.DYNAMIC;
            List<com.example.hw8_113306086.webTree.WebTree> trees = TreeBuilder.buildTreesFromLines(crawler1.resultsAndItsKids(isStaticMode),fetchMode);
            // 算分喔
            /*int i=0;
            for( com.example.hw8_113306086.webTree.WebTree tree: trees){
                System.out.println("第" + i + "網站樹：");
                try {
                    tree.root.setNodeScore(keywords);
                     System.out.println("網站分數：" + tree.root.nodeScore);
                } catch (IOException e) {
                    // 記錄錯誤並繼續處理其他樹
                    e.printStackTrace();
                }
                i++;
            }*/
           
            /*trees.parallelStream().forEach(tree -> {
                 try {
                    System.out.println("第" + i + "網站樹：");
                    tree.root.setNodeScore(keywords);
                    System.out.println("網站分數：" + tree.root.nodeScore);
                 } catch (IOException e) {
                         e.printStackTrace();
                }
        });*/
        ForkJoinPool customThreadPool = new ForkJoinPool(4);
        try {
            
            // 2. 將 parallelStream 包在 submit 裡面執行
            // 這樣 Stream 就會使用我們自定義的 Pool，而不是系統預設的
            customThreadPool.submit(() -> 
                trees.parallelStream().forEach(tree -> {
                    try {
                        System.out.println("正在處理網站樹 (Thread: " + Thread.currentThread().getName() + ")");
                        tree.root.setNodeScore(keywords);
                        System.out.println("網站分數：" + tree.root.nodeScore);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
            ).get(); // .get() 會等待所有任務完成才繼續往下走

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // 3. 關閉 Pool，釋放資源
            customThreadPool.shutdown();
        }


            // 依網站總分排序（descending）
            trees.sort(Comparator.comparingDouble(WebTree::getScore).reversed());
            List<SearchResultDTO> dtoList = new ArrayList<>();
            for (WebTree tree : trees) {
                SearchResultDTO dto = new SearchResultDTO();
                dto.setTitle(tree.root.webPage.name);
                dto.setUrl(tree.root.webPage.url);
                dto.setScore(tree.getScore());
                dtoList.add(dto);
            }
            model.addAttribute("results", dtoList);
            //新增推薦字list
            List<String> relatedKeywords = extractRelatedKeywords(trees, query);
            model.addAttribute("relatedKeywords", relatedKeywords);
           
            // 2. 將結果放進 Model
            //model.addAttribute("results", searchResults);
            model.addAttribute("query", query); // 把查詢詞也傳回去，可以顯示在搜尋框
        }

        // 3. 返回 index.html 頁面 
        return "index";
    }
    
    public static boolean containsChinese(String str) {
        return str.matches(".*[\\u4E00-\\u9FFF].*");
    }
   private List<String> extractRelatedKeywords(List<WebTree> trees, String originalQuery) {
    Map<String, Integer> tokenFreq = new HashMap<>();
    // 為了避免結果包含搜尋的詞 (case-insensitive)，先轉小寫存起來
    String queryLower = originalQuery.toLowerCase();
    
    int analyzeLimit = Math.min(trees.size(), 5);

    for (int i = 0; i < analyzeLimit; i++) {
        String content = trees.get(i).root.webPage.getContent();
        if (content == null || content.isEmpty()) continue;

        // 1. 利用正規表達式依據「非文字字元」切分
        // [^\\w\\u4e00-\\u9fa5]+ 代表：除了「英數底線」和「中文字」以外的所有符號（如空白、逗號、句號）都當作分隔符
        String[] segments = content.split("[^\\w\\u4e00-\\u9fa5]+");

        for (String seg : segments) {
            if (seg.isBlank()) continue;

            // 2. 判斷是「純英文/數字」還是「包含中文」
            if (seg.matches("^[a-zA-Z0-9_-]+$")) {
                // --- 英文處理邏輯 (完整單字) ---
                String word = seg.toLowerCase();
                
                // 過濾掉太短的字 (例如 's, t, 1, 2)
                if (word.length() < 3) continue;
                // 過濾停用詞
                if (STOP_WORDS.contains(word)) continue;
                // 過濾原本查詢詞
                if (queryLower.contains(word)) continue;

                tokenFreq.put(word, tokenFreq.getOrDefault(word, 0) + 1);

            } else {
                // --- 中文處理邏輯 (Bigram) ---
                // 這裡 seg 可能是 "蘋果手機" 或 "Hello你好"
                for (int j = 0; j < seg.length() - 1; j++) {
                    String token = seg.substring(j, j + 2);
                    
                    // Bigram 必須包含至少一個中文字 (避免把 "12" 或 "aB" 這種殘留的非中文抓進來)
                    if (!token.matches(".*[\\u4e00-\\u9fa5].*")) continue;
                    
                    if (STOP_WORDS.contains(token)) continue;
                    if (originalQuery.contains(token)) continue; // 中文不必轉小寫，直接比對

                    tokenFreq.put(token, tokenFreq.getOrDefault(token, 0) + 1);
                }
            }
        }
    }

    // 3. 排序並取前 8 名
    return tokenFreq.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(8)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
}
}