package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
//import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class TestCases {
    public static WebDriver driver;
    String homepageurl = "https://www.flipkart.com/";
    WebDriverWait wait;
    Actions act;

    public static void main(String[] args) {

        TestCases t1 = new TestCases();

        t1.testCase01();
        t1.testCase02();
        t1.testCase03();

    }

    @BeforeMethod
    public void StartBrowser() {
        System.out.println("Creating browser instance");

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        act = new Actions(driver);
    }

    @AfterMethod
    public void endTest() {

        System.out.println("End Test: TestCases");
        driver.close();
        driver.quit();
    }

    @Test
    public void testCase01() {

        System.out.println("Start Test case: testCase01");
        boolean status;
        try {

            openURL(homepageurl);

            status = search("washing machine");
            Assert.assertTrue(status, "could not find search result");

            status = sortBy("Popularity");
            Assert.assertTrue(status, "Error while sorting by popularity");

            int count = countitems();
            if (count > 0) {
                System.out.println("Number of items having ratings greater than 4: " + count);
            } else {
                System.out.println("No items having ratings greater than 4.");
            }

        } catch (Exception e) {
            takescreenshot("TestCase01", "failed");
            e.printStackTrace();
        }

        System.out.println("end Test case: testCase01");
    }

    @Test
    public void testCase02() {

        System.out.println("Start Test case: testCase02");
        boolean status;
        try {
            openURL(homepageurl);

            status = search("iPhone");
            Assert.assertTrue(status, "could not find search result");

            printTitleAndDiscountOfItems();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("end Test case: testCase02");
    }

    @Test
    private void testCase03() {

        System.out.println("Start Test case: testCase03");
        boolean status;
        try {
            openURL(homepageurl);

            status = search("Coffee Mug");
            Assert.assertTrue(status, "could not find search result");

            // select 4* & above checkbox which apply on search result
            select4star();

            // print the Title and image URL of the 5 items with highest number of reviews
            printTitleAndImgUrl();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("end Test case: testCase03");
    }

    /*
     * To print the title and Image Url of filtered list of Coffee Mug:
     * 1. Identifying parent element and list of filtered data into list of
     * webelements
     * 2. traverse though list and fetch each webelements title, img url and rating
     * using chanined xpath
     * 3. store the fetched data into user defined datatype Item by calling
     * constructor of Item class
     * 4. sort the list of ItemClass objects based on ratings using comparator and
     * print top five elements which is having highest rating
     */
    private void printTitleAndImgUrl() throws InterruptedException {

        List<WebElement> filtered_result_list = driver.findElements(By.xpath("//div[contains(@data-id,'MUG')]"));
        wait.until(ExpectedConditions.visibilityOf(filtered_result_list.get(filtered_result_list.size() - 1)));

        List<Item> ItemList = new ArrayList<Item>();

        for (int i = 0; i < filtered_result_list.size(); i++) {

            WebElement item = filtered_result_list.get(i);

            String title = item.findElement(By.xpath(".//a[@class='wjcEIp']")).getAttribute("title");
            String imgurl = item.findElement(By.xpath(".//img[@class='DByuf4']")).getAttribute("src");
            String review_string = item.findElement(By.xpath(".//span[@class='Wphh3N']")).getText();
            int reviewno = convertdiscountoint(review_string);

            ItemList.add(new Item(title, imgurl, reviewno));

        }
        Collections.sort(ItemList, Comparator.comparingInt(Item::getNumberOfReviews).reversed());

        for (int i = 0; i < 5; i++) {

            Item item = ItemList.get(i);

            System.out.print(item.getTitle() + "   |   ");
            System.out.print(item.getImageUrl() + "   |   ");
            System.out.print(item.getNumberOfReviews());
            System.out.println();

        }
    }

    /* User defined class for storing Item details by creating objects */
    static class Item {

        private String title;
        private String imageUrl;
        private int numberOfReviews;

        public Item(String title, String imageUrl, int numberOfReviews) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.numberOfReviews = numberOfReviews;
        }

        public String getTitle() {
            return title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public int getNumberOfReviews() {
            return numberOfReviews;
        }

    }

    /*
     * method to select 4* above checkbox applying on search results and waiting for
     * the results to load
     */
    private void select4star() throws InterruptedException {

        WebElement checkbox_4star = driver.findElement(By.xpath("//div[contains(@title,'4')]"));
        checkbox_4star.click();

        Thread.sleep(3000);

        List<WebElement> filtered_result_list = driver.findElements(By.xpath("//div[contains(@data-id,'MUG')]"));
        wait.until(ExpectedConditions.visibilityOf(filtered_result_list.get(filtered_result_list.size() - 1)));

    }

    /*  */
    private void printTitleAndDiscountOfItems() {

        List<WebElement> discount_list = driver.findElements(By.xpath("//div[@class='UkUFwK']/span"));
        wait.until(ExpectedConditions.visibilityOf(discount_list.get(discount_list.size() - 1)));

        for (WebElement item : discount_list) {

            String discount_string = item.getText();
            int discount_int = convertdiscountoint(discount_string);

            if (discount_int > 17) {

                WebElement title = item.findElement(By.xpath(".//preceding::div[contains(text(),'iPhone')]"));
                System.out.print(title.getText() + " | ");
                System.out.print(discount_string);
                System.out.println();

            } else {

                continue;

            }
        }

    }

    private int convertdiscountoint(String discount_string) {
        try {
            return Integer.parseInt(discount_string.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int countitems() throws InterruptedException {
        Thread.sleep(5000);
        List<WebElement> sortby_result_list = driver
                .findElements(By.xpath("//div[@class='tUxRFH']//div[@class='XQDdHH']"));
        wait.until(ExpectedConditions.visibilityOf(sortby_result_list.get(sortby_result_list.size() - 1)));
        List<Double> ratings_list = new ArrayList<Double>();
        for (int i = 0; i < sortby_result_list.size(); i++) {
            WebElement rating = sortby_result_list.get(i);
            Double ratingvalue = (Double.parseDouble(rating.getText()));
            if (ratingvalue <= 4.0) {
                ratings_list.add(ratingvalue);
            }
        }
        return ratings_list.size();
    }

    private boolean sortBy(String sorttext) {
        try {
            WebElement sort_popularity = driver.findElement(By.xpath("//div[text()='Popularity']"));
            click(sort_popularity);
            List<WebElement> sortby_result_list = driver.findElements(By.xpath("//div[@class='tUxRFH']"));
            wait.until(ExpectedConditions.visibilityOf(sortby_result_list.get(sortby_result_list.size() - 1)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean search(String searchtext) {
        try {
            WebElement search_textbox = driver
                    .findElement(By.xpath("//input[@placeholder='Search for Products, Brands and More']"));
            entertext(search_textbox, searchtext);
            WebElement search_button = driver.findElement(By.xpath("//button[@type='submit']"));
            click(search_button);
            //List<WebElement> search_result_list =
            //driver.findElements(By.xpath("//div[@class='tUxRFH']"));
            //wait.until(ExpectedConditions.visibilityOf(search_result_list.get(search_result_list.size()-1)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void click(WebElement search_button) {
        wait.until(ExpectedConditions.elementToBeClickable(search_button));
        search_button.click();
    }

    private void entertext(WebElement search_textbox, String searchtext) {
        wait.until(ExpectedConditions.visibilityOf(search_textbox));
        search_textbox.clear();
        search_textbox.sendKeys(searchtext);
    }

    private void openURL(String url) {
        driver.get(url);
    }

    public void takescreenshot(String type, String message) {
        try {

            String timestamp = java.time.LocalDateTime.now().toString();
            String filename = String.format("%s_%s_%s.png", timestamp, type, message);
            String filepath = "src\\test\\Screenshots";

            File destfile = new File(filepath + "\\screenshot_" + filename);

            TakesScreenshot scrshot = (TakesScreenshot) driver;
            File srcfile = scrshot.getScreenshotAs(OutputType.FILE);

            FileUtils.copyFile(srcfile, destfile);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}