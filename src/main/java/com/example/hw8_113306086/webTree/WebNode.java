package com.example.hw8_113306086.webTree;
import java.io.IOException;
import java.util.ArrayList;

public class WebNode {
	public WebNode parent;
	public ArrayList<WebNode> children;
	public WebPage webPage;	//child element
	public double nodeScore;//main element This node's score += all its children・s nodeScore
	
	public WebNode(WebPage webPage){
		this.webPage = webPage;
		this.children = new ArrayList<WebNode>();
	}
	
	public void setNodeScore(ArrayList<Keyword> keywords) throws IOException{
		// YOUR TURN
		// 2. calculate the score of this node
		webPage.setScore(keywords);
		nodeScore = 0.0;
		for(WebNode child: children) {
			child.setNodeScore(keywords);
			nodeScore += child.nodeScore;
		}
		nodeScore += webPage.score;
		// this method should be called in post-order mode

		// You should do something like:
		// 		1. compute the score of this webPage
		// 		2. set this score to initialize nodeScore
		//		3. nodeScore must be the score of this webPage plus all children's nodeScore

			
	}
	
	public void addChild(WebNode child){
		//add the WebNode to its children list
		this.children.add(child);
		child.parent = this;
	}
	
	public boolean isTheLastChild(){
		if(this.parent == null) return true;
		ArrayList<WebNode> siblings = this.parent.children;
		
		return this.equals(siblings.get(siblings.size() - 1));
	}
	
	public int getDepth(){
		int retVal = 1;
		WebNode currNode = this;
		while(currNode.parent!=null){
			retVal ++;
			currNode = currNode.parent;
		}
		return retVal;
	}
}
