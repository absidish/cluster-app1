package selenium.common;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SeleniumUtil
{
    public static WebElement waitPresenceOfElement( By locator )
    {
        WebDriverWait wait = new WebDriverWait( CommonAuth.getDriver(), 3 );

        return wait.until( ExpectedConditions.visibilityOfElementLocated( locator ) );
    }
}
