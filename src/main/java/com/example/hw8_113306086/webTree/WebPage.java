package com.example.hw8_113306086.webTree;

import java.io.IOException;
import java.util.ArrayList;

public class WebPage {
	public String url;
	public String name;
    //靜態網站擷取：
	public WordCounter counter;
	
    //動態網站擷取：
    public DynamicWordCounter dynamicCounter;
	
    public double score;
    public FetchMode mode;
	public WebPage(String url,String name,FetchMode mode){
		this.url = url;
		this.name = name;
		this.mode = mode;
		if (mode == FetchMode.STATIC) {
            this.counter = new WordCounter(url);
        } else {
            this.dynamicCounter = new DynamicWordCounter(url);
        }
	}
    public WebPage(String url,FetchMode mode){
		this.url = url;
		this.name = "child";
		  if (mode == FetchMode.STATIC) {
            this.counter = new WordCounter(url);
        } else {
            this.dynamicCounter = new DynamicWordCounter(url);
        }
	}
	
	public void setScore(ArrayList<Keyword> keywords) throws IOException{
		// YOUR TURN
		// 1. calculate the score of this webPage
		this.score = 0.0;
		for(Keyword i: keywords) {
            //靜態網站擷取：
			//this.score+= counter.countKeyword(i.name) * i.weight;
            //testing score counting
            //System.out.println("keyword"+i+"當前score:"+this.score);

            //動態網站擷取：
            // this.score+= dynamicCounter.countKeyword(i.name) * i.weight;
            if (mode == FetchMode.STATIC) {
                this.score += counter.countKeyword(i.name) * i.weight;
            } else {
                this.score += dynamicCounter.countKeyword(i.name) * i.weight;
            }
            
		}
	}
    public String getContent() {
        if (mode == FetchMode.STATIC) {
            return counter.getContent();   // 你要在 WordCounter 補這個方法（下一段）
        } else {
            return dynamicCounter.getContent();
        }
    }
	
}