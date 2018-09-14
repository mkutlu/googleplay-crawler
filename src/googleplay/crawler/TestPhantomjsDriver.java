package googleplay.crawler;

import static googleplay.crawler.getApps.crawlApp;
import java.util.List;
import java.util.NoSuchElementException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author kworkstat2
 */
public class TestPhantomjsDriver {

    public static void main(String[] args) {

        TestPhantomjsDriver example = new TestPhantomjsDriver();

        
        example.runPhantomjsWebDriver("/store/apps/collection/topselling_new_free");

    }

    public void runPhantomjsWebDriver(String href) {
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
            driver.get("https://play.google.com" + href);
            scrollToBottom(driver, null, 4000);

            Thread.sleep(6000);

            // Print out yahoo home page title.
            System.out.println("Page title is: " + driver.getTitle());

            // Get yahoo search text box element.
            /*By searchBoxById = By.id("uh-search-box");
            WebElement searchBox = driver.findElement(searchBoxById);
            if (searchBox != null) {
                searchBox.sendKeys("selenium");
                System.out.println("Input search keyword success.");
            }*/
            /*
        cardDetails.forEach(element -> {
            crawlApp(element.attr("href"));
        });*/
            By resultListByXPath = By.xpath(".//div[contains(@class,'card')]/.//a[contains(@class,'card-click-target')]/@href");
            List resultElementList = driver.findElements(resultListByXPath);

            if (resultElementList != null) {
                int size = resultElementList.size();
                System.out.println("Search result list size = " + size);
                // Loop the result list.
                for (int i = 0; i < size; i++) {
                    WebElement resultElement = (WebElement) resultElementList.get(i);

                    try {
                        // Get result item title element by xpath.
                        System.out.println(resultElement.getText());
                        System.out.println(resultElement.toString());
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

    public synchronized WebDriver scrollToBottom(WebDriver driver, WebElement element, int time) throws InterruptedException {
        String oldpage = "";
        String newpage = "";

        do {
            By submitBtnById = By.id("show-more-button");
            WebElement submitBtn = driver.findElement(submitBtnById);
            if (submitBtn != null && submitBtn.isDisplayed()) {
                System.out.println(submitBtn.toString());
                submitBtn.click();
                System.out.println("More button clicked");
                Thread.sleep(3000);
            }
            oldpage = driver.getPageSource();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, (document.body.scrollHeight))");
            this.wait(time);
            newpage = driver.getPageSource();
        } while (!oldpage.equals(newpage));
        return driver;
    }
}
