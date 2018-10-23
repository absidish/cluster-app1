package selenium.environments;


import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import selenium.common.CommonAuth;
import selenium.common.PeersUtil;

import static java.util.concurrent.TimeUnit.SECONDS;

import static org.awaitility.Awaitility.await;
import static org.testng.Assert.assertEquals;


public class CreateEnvironmentTest extends CommonAuth
{
    private List<String> peerIds;

    @Before
    public void addPeersToFav() throws Exception
    {
        getDriver();
        peerIds = PeersUtil.addPeersToFav();
    }


    @After
    public void removePeersFromFav() throws Exception
    {
        getDriver();
        PeersUtil.removePeersFromFav( peerIds );
    }


    @Test
    public void testCreateEnvironment() throws Exception
    {
        int peersCount = 0;
        //Create env start
        driver.get( "https://devbazaar.subutai.io/users/530/environments" );

        driver.findElement(
                By.xpath( "(.//*[normalize-space(text()) and normalize-space(.)='TOOLS'])[1]/following::a[1]" ) )
              .click();
        assertEquals( "Create environment", driver.findElement( By.linkText( "Create environment" ) ).getText() );
        driver.findElement( By.linkText( "Create environment" ) ).click();

        List<WebElement> peersToDeployEnv = driver.findElements( By.className( "forms-checkbox" ) );

        String elementId = "";

        for ( WebElement webElement : peersToDeployEnv )
        {
            elementId = webElement.getAttribute( "id" );
            if ( elementId.contains( "peer" ) )
            {
                elementId = "chbox-" + elementId;
                driver.findElement( By.id( elementId ) ).click();
                peersCount++;
            }
        }

        driver.findElement( By.id( "js-peer-warning-next" ) ).click();
        driver.findElement( By.id( "js-search" ) ).click();
        driver.findElement( By.id( "js-search" ) ).clear();
        driver.findElement( By.id( "js-search" ) ).sendKeys( "de" );

        for ( int i = 0; i < peersCount; i++ )
        {
            driver.findElement( By.xpath(
                    "(.//*[normalize-space(text()) and normalize-space(.)='Templates not found.'])"
                            + "[1]/following::img[1]" ) ).click();
        }

        driver.findElement( By.linkText( "Save" ) ).click();
        driver.findElement( By.id( "environment-name" ) ).click();
        driver.findElement( By.id( "environment-name" ) ).clear();
        driver.findElement( By.id( "environment-name" ) ).sendKeys( "testenv" );
        driver.findElement( By.id( "env-save" ) ).click();
        await().atMost( 240, SECONDS ).until( isEnvironmentHealthy() );

        Thread.sleep( 1 * 1000 );
        driver.findElement( By.xpath( "//*[contains(text(), 'Destroy')]" ) ).click();

        driver.findElement( By.xpath( "//button[text()='Yes']" ) ).click();

        await().atMost( 180, SECONDS ).until( isEnvironmentDestroyed() );

        assertEquals( "Create environment", driver.findElement( By.linkText( "Create environment" ) ).getText() );
    }


    private Callable<Boolean> isEnvironmentHealthy()
    {
        return new Callable<Boolean>()
        {
            public Boolean call() throws Exception
            {
                return "HEALTHY".equals(
                        driver.findElement( By.id( "env-state" ) ).getText() ); // The condition that must be fulfilled
            }
        };
    }


    private Callable<Boolean> isEnvironmentDestroyed()
    {
        return new Callable<Boolean>()
        {
            public Boolean call() throws Exception
            {
                return "Create environment".equals( driver.findElement( By.linkText( "Create environment" ) )
                                                          .getText() ); // The condition that must be fulfilled
            }
        };
    }
}
