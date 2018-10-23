package selenium.user;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import selenium.common.CommonAuth;
import selenium.common.PeersUtil;

import static org.testng.Assert.assertEquals;


public class UserReserveDomainTest extends CommonAuth
{

    @Before
    public void addPeersToFav() throws Exception
    {
        getDriver();
    }


    @After
    public void removePeersFromFav() throws Exception
    {
        getDriver();
    }


    @Test
    public void testUserDomainReservation() throws Exception {

        driver.get("https://devbazaar.subutai.io/users/530/environments");

        driver.findElement( By.className( "js-profile-name" ) ).click();
        driver.findElement(By.linkText("Domains")).click();
        driver.findElement(By.linkText("Add new")).click();
        driver.findElement(By.id("domain")).click();
        driver.findElement(By.id("domain")).clear();
        driver.findElement(By.id("domain")).sendKeys("mytest7789");
        driver.findElement(By.id("check_btn")).click();

        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Check'])[1]/following::button[1]")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='mytest7789.envs.test.s.optdyn.com'])[1]/following::td[1]")).click();

        // ERROR: Caught exception [ERROR: Unsupported command [doubleClick | xpath=(.//*[normalize-space(text()) and normalize-space(.)='mytest7789.envs.test.s.optdyn.com'])[1]/following::td[1] | ]]
        assertEquals("RESERVED", driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='mytest7789.envs.test.s.optdyn.com'])[1]/following::td[1]")).getText());
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='RESERVED'])[1]/following::button[1]")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Do you really want to delete this domain?'])[1]/following::button[1]")).click();
    }
}
