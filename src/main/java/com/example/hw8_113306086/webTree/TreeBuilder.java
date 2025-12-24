package com.example.hw8_113306086.webTree;

import java.util.ArrayList;
import java.util.List;

public class TreeBuilder {

    /**
     * 將 Crawler 回傳的 List<String> 轉成多棵 WebTree
     * 每一行格式：parentUrl[,childUrl1][,childUrl2]
     */
    public static List<WebTree> buildTreesFromLines(List<String> lines,FetchMode mode) {
    List<WebTree> trees = new ArrayList<>();

    for (String line : lines) {
        if (line == null || line.isEmpty()) continue;

        String[] parts = line.split(",");
        if (parts.length == 0) continue;

        // 1. 找出第一個像 URL 的欄位 (http/https 開頭)
        int firstUrlIdx = -1;
        for (int i = 0; i < parts.length; i++) {
            String token = parts[i].trim();
            if (token.startsWith("http://") || token.startsWith("https://")) {
                firstUrlIdx = i;
                break;
            }
        }
        if (firstUrlIdx == -1) {
            // 沒有任何 URL，這行怪怪的，跳過
            continue;
        }

        // 2. parentName = URL 前面的東西全部 join 回去（原本的 title）
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < firstUrlIdx; i++) {
            if (i > 0) nameBuilder.append(",");
            nameBuilder.append(parts[i]);
        }
        String parentName = nameBuilder.length() > 0
                ? nameBuilder.toString().trim()
                : parts[firstUrlIdx].trim(); // 沒有 title 就用 url 當 name

        // 3. parentUrl = 第一個 URL
        String parentUrl = parts[firstUrlIdx].trim();
        if (parentUrl.isEmpty()) continue;

        WebPage rootPage = new WebPage(parentUrl, parentName,mode);
        WebTree tree = new WebTree(rootPage);
        WebNode root = tree.root;

        // 4. 後面所有「也是 URL」的 token 才當 child
        for (int i = firstUrlIdx + 1; i < parts.length; i++) {
            String token = parts[i].trim();
            if (!(token.startsWith("http://") || token.startsWith("https://"))) {
                // 不是 URL（例如 title 裡多出來的逗號殘渣），忽略
                continue;
            }

            WebPage childPage = new WebPage(token, token,mode);
            WebNode childNode = new WebNode(childPage);
            root.addChild(childNode);
        }

        trees.add(tree);
    }

    return trees;
}
}