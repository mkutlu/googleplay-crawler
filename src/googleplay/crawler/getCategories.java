/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googleplay.crawler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author MesutKutlu
 */
public class getCategories {
    
    public void getAllCategories() throws IOException {
        Document document = Jsoup.connect("https://play.google.com/store/apps").get();
        Elements categories = document.select(".child-submenu-link");
        SQLiteJDBC sql = new SQLiteJDBC();
        System.out.println("Categories are creating:");
        categories.forEach(category -> {
            System.out.println(category.attr("title")+" : "+category.attr("href"));
            sql.addCategoryEntry(category.attr("title"),category.attr("href"));   
        });
    }
}
