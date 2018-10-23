package selenium.common;


import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class CommonAuth
{
    protected static WebDriver driver;

    private static void init()
    {
        driver = new FirefoxDriver();
        Dimension d = new Dimension( 1600, 1200 );
        //Resize current window to the set dimension
        driver.manage().window().setSize( d );

        driver.manage().timeouts().implicitlyWait( 30, TimeUnit.SECONDS );

        driver.get( "https://devbazaar.subutai.io/login" );
        driver.findElement( By.linkText( "GO AHEAD" ) ).click();
        driver.findElement( By.name( "email_or_username" ) ).click();
        driver.findElement( By.name( "email_or_username" ) ).clear();
        driver.findElement( By.name( "email_or_username" ) ).sendKeys( "absidish" );
        driver.findElement( By.id( "loginPassword" ) ).clear();
        driver.findElement( By.id( "loginPassword" ) ).sendKeys( "Mmnbv1234" );
        driver.findElement( By.id( "subutai-login" ) ).click();
    }


    public static WebDriver getDriver()
    {
        if ( driver == null )
        {
            init();
        }

        return driver;
    }
}
