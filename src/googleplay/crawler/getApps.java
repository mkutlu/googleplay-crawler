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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

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
        public void runPhantomjsWebDriver(String appHref) {
        try {
            // Set executable file path to system variable phantomjs.binary.path's value.
            String phantomjsExeutableFilePath = "phantomjs-2.1.1-windows/bin/phantomjs.exe";
            System.setProperty("phantomjs.binary.path", phantomjsExeutableFilePath);

            // Initiate PhantomJSDriver.
            WebDriver driver = new PhantomJSDriver();

            /* If you want to see the browser action, you can uncomment this block of code to use Chrome.
			// Specify Chrome Driver executable file path.
		    String chromeDriverPath = "C:\\Workspace\\dev2qa.com\\Lib\\chromedriver_win32\\chromedriver.exe";
			 
			//Assign chrome driver path to system property "webdriver.chrome.driver"
			System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			  
			//Initiate Chrome driver instance.
			WebDriver driver = new ChromeDriver();
             */
            //Must make the web browser full size other wise it can not parse out result by xpath.
            driver.manage().window().maximize();
            driver.get("https://play.google.com" + appHref);
            scrollToBottom(driver, null, 500);

            Thread.sleep(3000);
            
            // Print out yahoo home page title.
            System.out.println("Page title is: " + driver.getTitle());

            // Get yahoo search text box element.
            By searchBoxById = By.id("uh-search-box");
            WebElement searchBox = driver.findElement(searchBoxById);
            // Set search keyword.
            if (searchBox != null) {
                searchBox.sendKeys("selenium");
                System.out.println("Input search keyword success.");
            }

            // Get yahoo search box submit element.
            By submitBtnById = By.id("uh-search-button");
            WebElement submitBtn = driver.findElement(submitBtnById);
            // Click submit button.
            if (submitBtn != null) {
                submitBtn.click();
                System.out.println("Submit search form success.");
            }

            Thread.sleep(3000);

            // Get search result element list by xpath in search result page. 
            By resultListByXPath = By.xpath("//ol[@class=\"mb-15 reg searchCenterMiddle\"]/li");
            List resultElementList = driver.findElements(resultListByXPath);

            if (resultElementList != null) {
                int size = resultElementList.size();
                System.out.println("Search result list size = " + size);
                // Loop the result list.
                for (int i = 0; i < size; i++) {
                    WebElement resultElement = (WebElement) resultElementList.get(i);

                    try {
                        // Get result item title element by xpath.
                        By titleByXPath = By.xpath(".//a");
                        WebElement titleELement = resultElement.findElement(titleByXPath);
                        String title = "";
                        if (titleELement != null) {
                            title = titleELement.getText();
                        }

                        if (!"".equals(title)) {
                            System.out.println("title = " + title);
                        }
                    } catch (NoSuchElementException ex) {
                        ex.printStackTrace();
                    }

                    try {
                        // Get result item description element by xpath.
                        By descByXPath = By.xpath(".//div[@class=\"compText aAbs\"]");
                        WebElement descElement = resultElement.findElement(descByXPath);
                        String description = "";
                        if (descElement != null) {
                            description = descElement.getText();
                        }

                        if (!"".equals(description)) {
                            System.out.println("description = " + description);
                            System.out.println();
                        }
                    } catch (NoSuchElementException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            driver.quit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public synchronized WebDriver scrollToBottom(WebDriver driver, WebElement element,int time) throws InterruptedException {
     String oldpage="";
     String newpage="";


     do{
         oldpage=driver.getPageSource();
         ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, (document.body.scrollHeight))");
         this.wait(time);
         newpage=driver.getPageSource();
    }while(!oldpage.equals(newpage));
        return driver;
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
