package selenium.environments;


import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import selenium.common.CommonAuth;
import selenium.common.PeersUtil;
import selenium.common.SeleniumUtil;

import static java.util.concurrent.TimeUnit.SECONDS;

import static com.gargoylesoftware.htmlunit.HttpHeader.USER_AGENT;
import static org.awaitility.Awaitility.await;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class CreateEnvironmentFromUserReposTest extends CommonAuth
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
    public void createEnvAndCheckPortMap() throws Exception
    {
        createEnv();

        Thread.sleep( 2 * 1000 );

        isPeersReady();

        isContainersRunning();

        isPortMappingCreated();

        isSshKeysWork();

        addPeerToEnvironment();

        Thread.sleep( 2 * 1000 );

        destroyEnvironment();
    }


    private void addPeerToEnvironment() throws Exception
    {
        int peersCount = 0;
        
        driver.findElement( By.xpath( "//*[contains(text(), 'Peers') and contains(@class, 'header-tabs-item')]" ) )
              .click();

        driver.findElement(By.linkText("Add peers")).click();

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

        driver.findElement(By.id("js-peer-warning-next")).click();
        driver.findElement(By.id("js-search")).click();
        driver.findElement(By.id("js-search")).clear();
        driver.findElement(By.id("js-search")).sendKeys("deb");

        for ( int i = 0; i < peersCount; i++ )
        {
            driver.findElement( By.xpath(
                    "(.//*[normalize-space(text()) and normalize-space(.)='Templates not found.'])"
                            + "[1]/following::img[1]" ) ).click();
        }


        driver.findElement(By.linkText("Apply")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='No changes detected.'])[1]/following::button[1]")).click();

        await().atMost( 500, SECONDS ).until( isEnvironmentHealthy() );
    }


    private void isSshKeysWork() throws Exception
    {
        WebElement element = null;
        driver.findElement( By.xpath( "//*[contains(text(), 'SSH Keys')]" ) ).click();
        Thread.sleep( 1000 );

        element = SeleniumUtil.waitPresenceOfElement( By.id( "add-ssh-key" ) );
        element.click();

        driver.findElement( By.id( "name" ) ).clear();
        driver.findElement( By.id( "name" ) ).sendKeys( "testsshkey" );
        driver.findElement( By.id( "key" ) ).click();
        driver.findElement( By.id( "key" ) ).clear();
        driver.findElement( By.id( "key" ) ).sendKeys(
                "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDQNXl+vAiN9GsWSXys58pk8ogrn/BjTy+WdCju4XYfgKbjBAL37eei"
                        + "+I6fTBJ42jZ5Y1irq/H5wPYeNt55VVdOonJJ52YLDnCs66PPUID8zXFn5aQv9Ti9k+qxZX6"
                        +
                        "/3Rbx7FhKyxNP1SOZ9TcQVLPlZtY3o6yfxp32Wf92A14iHUY80XWkLhe6BeKWM68DKLfafNIcRD30jRh0vtyUQaTOV9VC+g3nX1AKcVnLufvXaJ4144ZsJSRDbxJDT2WxVgazkCGvG7ki0qQzfTnRUbFbd9l5DDET42QpLu4z5fI1LN/I+ickz6WKJjs4yvZQcfwHGipaJdQhf3Gco0JvpsNf root@scp" );

        driver.findElement(
                By.xpath( "(.//*[normalize-space(text()) and normalize-space(.)='Browse'])[1]/following::button[1]" ) )
              .click();

        await().atMost( 500, SECONDS ).until( isEnvironmentHealthy() );
    }


    private void destroyEnvironment()
    {
        driver.findElement( By.xpath( "//*[contains(text(), 'Destroy')]" ) ).click();

        driver.findElement( By.xpath(
                "(.//*[normalize-space(text()) and normalize-space(.)='Do you want to destroy environment wp?'])"
                        + "[1]/following::button[1]" ) )
              .click();

        await().atMost( 300, SECONDS ).until( isEnvironmentDestroyed() );

        assertEquals( "Create environment", driver.findElement( By.linkText( "Create environment" ) ).getText() );
    }


    private void createEnv()
    {
        WebElement element = null;

        driver.get( "https://devbazaar.subutai.io/users" );

        driver.findElement( By.className( "js-profile-name" ) ).click();

        driver.findElement( By.linkText( "GitHub projects" ) ).click();

        driver.findElement(
                By.xpath( "(.//*[normalize-space(text()) and normalize-space(.)='master'])[2]/following::a[1]" ) )
              .click();

        element = SeleniumUtil.waitPresenceOfElement( By.id( "bp-wizard-start" ) );
        element.click();

        element = SeleniumUtil.waitPresenceOfElement( By.id( "bp-wizard-peers-next" ) );
        element.click();

        element = SeleniumUtil.waitPresenceOfElement( By.id( "bp-wizard-port-next" ) );
        element.click();

        element = SeleniumUtil.waitPresenceOfElement( By.id( "bp-wizard-finish" ) );
        element.click();

        await().atMost( 500, SECONDS ).until( isEnvironmentHealthy() );
    }


    private void isPortMappingCreated() throws Exception
    {
        driver.findElement(
                By.xpath( "//*[contains(text(), 'Container Port Mapping') and contains(@class, 'header-tabs-item')]" ) )
              .click();

        List<WebElement> containerStates = driver.findElements( By.className( "js-cont-port-map-state" ) );

        for ( WebElement conState : containerStates )
        {
            assertTrue( conState.getText().equals( "USED" ) );
        }

        List<WebElement> proxyStates = driver.findElements( By.className( "js-proxy-state" ) );

        for ( WebElement conState : proxyStates )
        {
            assertTrue( conState.getText().equals( "READY" ) );
        }

        List<WebElement> proxyStates1 = driver.findElements( By.className( "js-cont-proxy-port-map-state" ) );

        for ( WebElement conState : proxyStates1 )
        {
            assertTrue( conState.getText().equals( "USED" ) );
        }

        String url = "http://wpst.envs.test.s.optdyn.com";

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet( url );

        // add request header
        request.addHeader( "User-Agent", USER_AGENT );
        HttpResponse response = client.execute( request );

        assertEquals( 200, response.getStatusLine().getStatusCode() );
    }


    private void isContainersRunning()
    {
        driver.findElement( By.xpath( "//*[contains(text(), 'Containers') and contains(@class, 'header-tabs-item')]" ) )
              .click();

        List<WebElement> containerStates = driver.findElements( By.className( "js-env-container-state" ) );

        for ( WebElement conState : containerStates )
        {
            assertTrue( conState.getText().equals( "RUNNING" ) );
        }
    }


    private void isPeersReady()
    {
        driver.findElement( By.xpath( "//*[contains(text(), 'Peers') and contains(@class, 'header-tabs-item')]" ) )
              .click();

        assertTrue( driver.findElement( By.className( "js-env-peer-state" ) ).getText().equals( "READY" ) );
    }


    private Callable<Boolean> isEnvironmentHealthy()
    {
        return new Callable<Boolean>()
        {
            public Boolean call() throws Exception
            {
                try
                {
                    return "HEALTHY".equals( driver.findElement( By.id( "env-state" ) )
                                                   .getText() ); // The condition that must be fulfilled
                }
                catch ( Exception e )
                {
                    return false;
                }
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
