
package com.example.hw8_113306086.webTree;


import java.util.ArrayList;
import java.io.IOException;
public class WebTree {
	public WebNode root;
	public double score;
	public WebTree(WebPage rootPage){
		this.root = new WebNode(rootPage);
	}
	
	public void setPostOrderScore(ArrayList<Keyword> keywords) throws IOException{
		setPostOrderScore(root, keywords);
        score = root.nodeScore;

	}

	private void setPostOrderScore(WebNode startNode, ArrayList<Keyword> keywords) throws IOException{
		// YOUR TURN
		// 3. compute the score of children nodes via post-order, then setNodeScore for startNode
		startNode.setNodeScore(keywords);
	}
	
	public void eularPrintTree(){
		eularPrintTree(root);
	}
	
	private void eularPrintTree(WebNode startNode){
		// YOUR TURN
		//  4. Implement a recursive method eularPrintTree(WebNode startNode)
		//  that prints the entire tree structure using parentheses to show hierarchy.

		/* Example output:
		(Soslab,459.0
			(Publication,286.2)
			(Projects,42.0
				(Stranger,0.0)
			)
			(Member,12.0)
			(Course,5.4)
		)*/
		// Hints:
		//  - Can use the function getDepth() and isTheLastChild() in WebNode.java
		//  - The opening "(" comes before printing the node name and score,
		//    and the closing ")" comes after printing all of its children.
		//  - Use something like repeat("\t", n) for indentation.
		//  */
		String indent = repeat("\t", startNode.getDepth() - 1);
		System.out.print(indent + "(" + startNode.webPage.name + "," + startNode.nodeScore);
		for (WebNode child : startNode.children) {
			System.out.println();
			eularPrintTree(child);
			
		}
		if(!startNode.children.isEmpty()) {
			System.out.println();
			System.out.print(indent + ")");
		}
		else {
			System.out.print(")");}
		
		
		
	}
	
	private String repeat(String str,int repeat){
		String retVal  = "";
		for(int i=0;i<repeat;i++){
			retVal+=str;
		}
		return retVal;
	}

	// 返回整棵樹（root）的累積分數，供排序等用途使用
	public double getScore(){
		if(this.root == null) return 0.0;
		return this.root.nodeScore;
	}
}