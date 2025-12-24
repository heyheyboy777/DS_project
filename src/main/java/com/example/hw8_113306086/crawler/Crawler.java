package com.example.hw8_113306086.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Crawler：從 CSE 回傳的結果中，對每個網址最多再抓 2 個子網址。
 * 回傳格式：每個元素是一個 String：
 *   parentUrl,childUrl,childUrl
 */
public class Crawler {

    private final Map<String, String> cseResults;

    public Crawler(Map<String, String> cseResults) {
        this.cseResults = cseResults;
    }

    /** 取得 host，例如 https://www.op.gg/... -> www.op.gg */
    private String extractHost(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            return host == null ? "" : host.toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 取得 root domain，用來把子網域視為同一個網站：
     * 例：
     *   www.op.gg                  -> op.gg
     *   zh.wikipedia.org           -> wikipedia.org
     *   play.google.com            -> google.com
     *   www.austinhome.com.tw      -> austinhome.com.tw
     */
    private String extractRootDomain(String url) {
        String host = extractHost(url);
        if (host.isEmpty()) return "";

        if (host.startsWith("www.")) {
            host = host.substring(4);
        }

        String[] parts = host.split("\\.");
        if (parts.length <= 2) {
            return host;
        }

        // 假設最後一段長度為 2 的是國家碼，例如 com.tw, com.hk
        String last = parts[parts.length - 1];
        if (last.length() == 2 && parts.length >= 3) {
            // 取最後三段：xxx.com.tw
            return parts[parts.length - 3] + "." + parts[parts.length - 2] + "." + parts[parts.length - 1];
        } else {
            // 否則取最後兩段：op.gg, google.com, wikipedia.org
            return parts[parts.length - 2] + "." + parts[parts.length - 1];
        }
    }

    /** 某些網站乾脆整站略過（reddit 幾乎都 403） */
    private boolean isBlockedSite(String url) {
        String host = extractHost(url);
        if (host.isEmpty()) return false;
        return host.contains("reddit.com");
        // 需要的話可以加：
        // || host.contains("facebook.com")
        // || host.contains("instagram.com")
    }

    /** 檢查 href 是否是「基本合理」的超連結 */
    private boolean isBasicValidHref(String href, String parentUrl) {
        if (href == null || href.isEmpty()) return false;
        if (!href.startsWith("http://") && !href.startsWith("https://")) return false;
        if (href.contains("javascript:")) return false;
        if (href.equals(parentUrl)) return false;
        return true;
    }

    /**
     * 回傳：每筆 CSE 結果 + 最多兩個子網址
     * 每個元素是：parentUrl[,childUrl][,childUrl]
     */
    public List<String> resultsAndItsKids(boolean staticOrDynamic) {
        List<String> results = new ArrayList<>();

        for (Map.Entry<String, String> entry : cseResults.entrySet()) {

            String parentUrl = entry.getValue();
            String parentRootDomain = extractRootDomain(parentUrl);
            String parentName = entry.getKey();
            StringBuilder sb = new StringBuilder(parentName);
            sb.append(",").append(parentUrl);

            if (!staticOrDynamic) { // dynamic：只回傳 root
            results.add(sb.toString());
            continue;
            }
            
            System.out.println("\n=== Parent: " + parentUrl + " (rootDomain=" + parentRootDomain + ") ===");

            // 先避開一定會失敗的站（例如 reddit）
            if (isBlockedSite(parentUrl)) {
                System.out.println("  >> Blocked site, skip crawling children.");
                results.add(sb.toString());
                continue;
            }

            try {
                Document doc = Jsoup.connect(parentUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(5000)
                        .get();

                Elements links = doc.select("a[href]");
                Set<String> addedChildren = new LinkedHashSet<>();
                int childCount = 0;

                // -------- 第一輪：先找「同根網域」的子頁 --------
                for (Element link : links) {
                    if (childCount >= 2) break;

                    String href = link.attr("abs:href"); // 會自動轉成絕對網址

                    if (!isBasicValidHref(href, parentUrl)) continue;
                    if (href.contains("#")) continue; // 先略過同頁 anchor

                    String childRootDomain = extractRootDomain(href);
                    if (childRootDomain.isEmpty()) continue;
                    if (!childRootDomain.equals(parentRootDomain)) continue;

                    if (addedChildren.add(href)) {
                        System.out.println("  ✅ [same-domain child] " + href);
                        sb.append(",").append(href);
                        childCount++;
                    }
                }

                // -------- 第二輪：Fallback，如果同站找不到 2 個，就放寬條件 --------
                if (childCount < 2) {
                    System.out.println("  >> Fallback: not enough same-domain children, try any http(s) links");

                    for (Element link : links) {
                        if (childCount >= 2) break;

                        String href = link.attr("abs:href");
                        if (!isBasicValidHref(href, parentUrl)) continue;
                        if (href.contains("#")) continue;

                        // 避免抓到我們不想要的社群/廣告站
                        String host = extractHost(href);
                        if (host.contains("facebook.com")
                                || host.contains("instagram.com")
                                || host.contains("twitter.com")
                                || host.contains("x.com")
                                || host.contains("reddit.com")
                                || host.contains("youtube.com")
                                || host.contains("tiktok.com")) {
                            continue;
                        }

                        if (addedChildren.add(href)) {
                            System.out.println("  ✅ [fallback child] " + href);
                            sb.append(",").append(href);
                            childCount++;
                        }
                    }
                }

                System.out.println("  >>> total children = " + childCount);

            } catch (IOException e) {
                System.err.println("FAILED to fetch " + parentUrl + ": " + e.getMessage());
            }

           
        
        results.add(sb.toString());}
        System.out.println(results);
        return results;
        }
}
