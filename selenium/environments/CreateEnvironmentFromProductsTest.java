package selenium.environments;


import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;

import selenium.common.CommonAuth;
import selenium.common.PeersUtil;
import selenium.common.SeleniumUtil;

import static java.util.concurrent.TimeUnit.SECONDS;

import static org.awaitility.Awaitility.await;
import static org.testng.Assert.assertEquals;


public class CreateEnvironmentFromProductsTest extends CommonAuth
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
    public void testBPBuild() throws Exception
    {

        WebElement element = null;
        List<String> peerIds = Lists.newArrayList();
        String peerId = "";

        driver.get( "https://devbazaar.subutai.io/peers" );

        List<WebElement> peers = driver.findElements( By.className( "js-add-to-favorite" ) );

        for ( WebElement peer : peers )
        {
            peerId = peer.getAttribute( "id" );
            peerIds.add( peerId );
            element = SeleniumUtil.waitPresenceOfElement( By.id( peerId ) );
            await().atMost( 5, SECONDS ).until( isPeerAddedToFav( peerId ) );
            element.click();
        }

        driver.findElement(
                By.xpath( "(.//*[normalize-space(text()) and normalize-space(.)='Peers'])[3]/following::span[1]" ) )
              .click();
        driver.findElement( By.linkText( "app1-1" ) ).click();
        driver.findElement( By.linkText( "Build" ) ).click();

        element = SeleniumUtil.waitPresenceOfElement( By.id( "bp-wizard-start" ) );
        element.click();

        element = SeleniumUtil.waitPresenceOfElement( By.id( "bp-wizard-peers-next" ) );
        element.click();

        element = SeleniumUtil.waitPresenceOfElement( By.id( "bp-wizard-port-next" ) );
        element.click();

        element = SeleniumUtil.waitPresenceOfElement( By.id( "bp-wizard-finish" ) );
        element.click();

        driver.findElement( By.xpath(
                "(.//*[normalize-space(text()) and normalize-space(.)='Applications'])[1]/following::div[1]" ) )
              .click();

        await().atMost( 60 * 15, SECONDS ).until( isEnvironmentHealthy() );
        Thread.sleep( 1 * 1000 );
        driver.findElement( By.xpath( "//*[contains(text(), 'Destroy')]" ) ).click();

        driver.findElement( By.xpath( "//button[text()='Yes']" ) ).click();

        await().atMost( 60 * 2, SECONDS ).until( isEnvironmentDestroyed() );

        assertEquals( "Create environment", driver.findElement( By.linkText( "Create environment" ) ).getText() );


        //        remove peers from favorites start
        driver.get( "https://devbazaar.subutai.io/peers" );
        for ( String id : peerIds )
        {
            element = SeleniumUtil.waitPresenceOfElement( By.id( id ) );
            element.click();
        }
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


    private Callable<Boolean> isPeerAddedToFav( String elementId )
    {
        return new Callable<Boolean>()
        {
            public Boolean call() throws Exception
            {
                return "Add to Favorites".equals(
                        driver.findElement( By.id( elementId ) ).getText() ); // The condition that must be fulfilled
            }
        };
    }
}
