package test;
import org.openqa.selenium.TakesScreenshot;

public class takeScreenShot {
    
    WebDriver driver;
    String type;
    String message;

    public void takescreenshot(WebDriver driver, String type, String message){
        
        String timestamp = java.time.LocalDateTime.now();
        String filename = String.format("%s_%s_%s.png",timestamp,type,message);
        String filepath = "src\\test\\Utility";

        File destfile = new File(filepath+"/screenshot/"+filename);
        
        TakesScreenshot scrshot = (TakesScreenshot) driver;
        File srcfile = scrshot.getScreenshotAs(OutputType.FILE);

        FileUtils.copyFile(srcfile,destfile);

        
    }
}
