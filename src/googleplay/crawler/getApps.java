/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googleplay.crawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
            System.out.println(href + " CRAWLING :");
            getFunction(href);
        } else {
            categories.forEach(category -> {
                try {
                    System.out.println(category.attr("href") + " CRAWLING :");
                    getFunction(category.attr("href"));
                } catch (IOException ex) {
                    Logger.getLogger(getApps.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    public void getFunction(String href) throws IOException {
        /*System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        WebDriver webDriver = new FirefoxDriver();
        webDriver.get("http://play.google.com"+href);
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
        }*/
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        //HTMLPage page = webClient.get("https://play.google.com" + href);

        Document document = Jsoup.connect("https://play.google.com" + href).get();
        Elements card = document.select(".card");

        Elements cardDetails = card.select(".card-click-target");
        cardDetails.forEach(element -> {
            crawlApp(element.attr("href"));
        });
    }

    public static void crawlApp(String appHref) {
        try {
            String name, update = "", mail = "", id = "", category = "";
            Document document = Jsoup.connect("https://play.google.com" + appHref).get();

            Elements nameTag = document.select(".AHFaub span");
            name = nameTag.text();

            Elements catTag = document.select("a");
            for (Element tag : catTag) {
                if (tag.attr("itemprop").equals("genre")) {
                    category = tag.text();
                }
            }
            id = appHref.substring(appHref.indexOf("=") + 1);

            Elements aTags = document.select(".htlgb a");
            for (Element tag : aTags) {
                System.out.println("mail:" + tag.text());
                if (tag.text().contains("@")) {
                    mail = tag.text();
                }
            }

            Elements spanTags = document.select(".htlgb span");
            for (Element tag : spanTags) {
                System.out.println("date:" + tag.text());
                String[] spl = tag.text().replace(",", "").split("\\s+");
                if (spl.length == 3) {
                    if ((spl[0].matches("\\d+") && spl[2].matches("\\d+") || (spl[1].matches("\\d+") && spl[2].matches("\\d+")))) {
                        update = tag.text();
                    }
                }
            }
            SQLiteJDBC sql = new SQLiteJDBC();
            sql.addAppEntry(id, name, category, update, mail);
        } catch (IOException ex) {
            Logger.getLogger(getApps.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
