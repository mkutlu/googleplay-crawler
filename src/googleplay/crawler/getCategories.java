/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googleplay.crawler;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author MesutKutlu
 */
public class getCategories {
    
    public ArrayList getAllCategories() throws IOException {
        Document document = Jsoup.connect("https://play.google.com/store/apps").get();
        Elements categories = document.select(".child-submenu-link");
        SQLiteJDBC sql = new SQLiteJDBC();
        ArrayList<String> arr = new  ArrayList<>();
        System.out.println("Categories are creating:");
        categories.forEach(category -> {
            System.out.println(category.attr("title")+" : "+category.attr("href"));
            arr.add(category.attr("title")+" : "+category.attr("href"));
            sql.addCategoryEntry(category.attr("title"),category.attr("href"));   
        });
        return arr;
    }
}
