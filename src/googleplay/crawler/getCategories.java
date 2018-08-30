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
    public static void main(String [] args){
        try {
            getAllCategories();
        } catch (IOException ex) {
            Logger.getLogger(getCategories.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void getAllCategories() throws IOException {
        Document document = Jsoup.connect("https://play.google.com/store/apps").get();
        Elements categories = document.select(".child-submenu-link");
        categories.forEach(category -> System.out.println(category.attr("title")));
    }
}
