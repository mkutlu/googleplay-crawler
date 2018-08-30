/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googleplay.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author MesutKutlu
 */
public class getApps {

    public void getAllApps(String href) throws IOException {
        Document document = Jsoup.connect("https://play.google.com" + href).get();
        Elements categories = document.select(".see-more");
        if (categories.isEmpty()) {
            getFunction(href);
        } else {
            categories.forEach(category -> {
                System.out.println(category.text() + " : " + category.attr("href"));
                try {
                    getFunction(category.attr("href"));
                } catch (IOException ex) {
                    Logger.getLogger(getApps.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        SQLiteJDBC sql = new SQLiteJDBC();
        ArrayList<String> arr = new ArrayList<>();
        System.out.println("Categories are creating:");
        categories.forEach(category -> {
            System.out.println(category.attr("title") + " : " + category.attr("href"));
            arr.add(category.attr("title") + " : " + category.attr("href"));
            sql.addCategoryEntry(category.attr("title"), category.attr("href"));
        });
    }

    public void getFunction(String href) throws IOException {
        System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        WebDriver webDriver = new FirefoxDriver();
        try {
            long lastHeight = (long) ((JavascriptExecutor) webDriver).executeScript("return document.body.scrollHeight");

            while (true) {
                ((JavascriptExecutor) webDriver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(2000);

                long newHeight = (long) ((JavascriptExecutor) webDriver).executeScript("return document.body.scrollHeight");
                if (newHeight == lastHeight) {
                    break;
                }
                lastHeight = newHeight;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       Document document = Jsoup.connect("https://play.google.com" + href).get();
       Elements categories = document.select(".card");
       System.out.println(categories.size());
    }
}
