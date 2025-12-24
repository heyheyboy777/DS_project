package com.example.hw8_113306086.webTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;
 
public class WordCounter {
	private String urlStr;
    private String content;
    
    public WordCounter(String urlStr){
    	this.urlStr = urlStr;
    }
    
    private String fetchContent() throws IOException{
        if (urlStr.contains("reddit.com") ||
        urlStr.contains("facebook.com") ||
        urlStr.contains("threads.com") ||
        urlStr.contains("instagram.com")) {
            return "";
        }
    try {
        Document doc = Jsoup.connect(this.urlStr)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                           "AppleWebKit/537.36 (KHTML, like Gecko) " +
                           "Chrome/115.0 Safari/537.36")
                .referrer("https://www.google.com/") // 有些站會看 referrer，順便加一下
                .timeout(5000) // 1.5 秒超時
                .followRedirects(true)
                .get();
        return doc.body() != null ? doc.body().text() : "";
    } catch (org.jsoup.HttpStatusException e) {
        // 例如 403/404 時就當作「抓不到內容」，不要讓整個流程爆掉
        System.err.println("HTTP " + e.getStatusCode() +
                " for " + this.urlStr + " -> treat as empty page");
        return "";
    } catch (IllegalArgumentException iae) {
        // URL 本身怪怪的（例如 "no learning ..."）, 直接當作空內容
        System.err.println("Invalid URL: " + this.urlStr +
                " -> treat as empty page");
        return "";
    } catch (IOException ioe) {
        // 其他 IO 問題，也不要讓它炸掉
        System.err.println("Failed to fetch " + this.urlStr +
                ": " + ioe.getMessage() + " -> treat as empty page");
            return "";
        }
    }   

    public String getContent() {
        return content == null ? "" : content;
    }
    public int countKeyword(String keyword) throws IOException{
		if (content == null){
		    content = fetchContent();
		}
		
		//To do a case-insensitive search, we turn the whole content and keyword into upper-case:
		//content = content.toUpperCase();
		//keyword = keyword.toUpperCase();
	
		int retVal = 0;
		int fromIdx = 0;
		int found = -1;
	
		while ((found = content.indexOf(keyword, fromIdx)) != -1){
		    retVal++;
		    fromIdx = found + keyword.length();
		}
	
		return retVal;
    }
}