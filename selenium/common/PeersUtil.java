package selenium.common;


import java.util.List;
import java.util.concurrent.Callable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;

import static java.util.concurrent.TimeUnit.SECONDS;

import static org.awaitility.Awaitility.await;


public class PeersUtil
{
    public static  List<String> addPeersToFav()
    {
        List<String> peerIds = Lists.newArrayList();
        String peerId = "";
        WebElement element = null;

        CommonAuth.getDriver().get( "https://devbazaar.subutai.io/peers" );

        List<WebElement> peers = CommonAuth.getDriver().findElements( By.className( "js-add-to-favorite" ) );

        for ( WebElement peer : peers )
        {
            peerId = peer.getAttribute( "id" );
            peerIds.add( peerId );
            element = SeleniumUtil.waitPresenceOfElement( By.id( peerId ) );
            await().atMost( 5, SECONDS ).until( isPeerAddedToFav( peerId ) );
            element.click();
        }

        return peerIds;
    }


    public static void removePeersFromFav( List<String> peerIds )
    {
        WebElement element = null;

        CommonAuth.getDriver().get( "https://devbazaar.subutai.io/peers" );

        for ( String id : peerIds )
        {
            element = SeleniumUtil.waitPresenceOfElement( By.id(id) );
            element.click();
        }
    }


    private static  Callable<Boolean> isPeerDeletedToFav( String elementId )
    {
        return new Callable<Boolean>()
        {
            public Boolean call() throws Exception
            {
                return "Remove from Favorites".equals( CommonAuth.getDriver().findElement( By.id( elementId ) )
                                                                 .getText() ); // The condition that must be fulfilled
            }
        };
    }


    private static  Callable<Boolean> isPeerAddedToFav( String elementId )
    {
        return new Callable<Boolean>()
        {
            public Boolean call() throws Exception
            {
                return "Add to Favorites".equals( CommonAuth.getDriver().findElement( By.id( elementId ) )
                                                            .getText() ); // The condition that must be fulfilled
            }
        };
    }
}
