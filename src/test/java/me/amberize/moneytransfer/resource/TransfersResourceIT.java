package me.amberize.moneytransfer.resource;

import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.junit.DropwizardAppRule;
import me.amberize.moneytransfer.App;
import me.amberize.moneytransfer.AppConfig;
import me.amberize.moneytransfer.dto.AccountDto;
import me.amberize.moneytransfer.dto.TransferDto;
import org.apache.ignite.Ignition;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class TransfersResourceIT {

    private static final DropwizardTestSupport<AppConfig> SUPPORT = new DropwizardTestSupport<>(
            App.class, null, ConfigOverride.config("server.applicationConnectors[0].port", "0"));

    @BeforeClass
    public void setUp() {
        Ignition.start();

        SUPPORT.before();
    }

    @AfterClass
    public void setDown() {
        SUPPORT.after();
    }

    @Test
    public void shouldExecuteTransfer() {
        Client client = new JerseyClientBuilder().build();

        AccountDto account1 = getAccount("Test1", UUID.randomUUID().toString(), "400");
        AccountDto account2 = getAccount("Test2", UUID.randomUUID().toString(), "500");

        Response response = client.target(String.format("http://localhost:%s/accounts", SUPPORT.getLocalPort()))
                .request()
                .post(Entity.json(account1));

        assertEquals(response.getStatus(), 200);

        response = client.target(String.format("http://localhost:%s/accounts", SUPPORT.getLocalPort()))
                .request()
                .post(Entity.json(account2));

        assertEquals(response.getStatus(), 200);

        List result = client.target(String.format("http://localhost:%s/accounts", SUPPORT.getLocalPort()))
                .request()
                .get(List.class);

        assertNotNull(result);
        assertTrue(result.size() == 2);

        TransferDto transferDto = new TransferDto();
        transferDto.setRecipientAccount(account1.getAccountNumber());
        transferDto.setSenderAccount(account2.getAccountNumber());
        transferDto.setSum("500");

        response = client.target(String.format("http://localhost:%s/transfers", SUPPORT.getLocalPort()))
                                  .request()
                                  .post(Entity.json(transferDto));

        assertEquals(response.getStatus(), 200);

        client.target(String.format("http://localhost:%s/accounts/%s", SUPPORT.getLocalPort(), account1.getAccountNumber()))
                .request()
                .delete();

        client.target(String.format("http://localhost:%s/accounts/%s", SUPPORT.getLocalPort(), account2.getAccountNumber()))
                .request()
                .delete();
    }

    @Test
    public void shouldNotExecuteTransfer() {
        Client client = new JerseyClientBuilder().build();

        AccountDto account1 = getAccount("Test3", UUID.randomUUID().toString(), "400");
        AccountDto account2 = getAccount("Test4", UUID.randomUUID().toString(), "200");

        Response response = client.target(String.format("http://localhost:%s/accounts", SUPPORT.getLocalPort()))
                .request()
                .post(Entity.json(account1));

        assertEquals(response.getStatus(), 200);

        response = client.target(String.format("http://localhost:%s/accounts", SUPPORT.getLocalPort()))
                .request()
                .post(Entity.json(account2));

        assertEquals(response.getStatus(), 200);

        List result = client.target(String.format("http://localhost:%s/accounts", SUPPORT.getLocalPort()))
                .request()
                .get(List.class);

        assertNotNull(result);
        assertTrue(result.size() == 2);

        TransferDto transferDto = new TransferDto();
        transferDto.setRecipientAccount(account1.getAccountNumber());
        transferDto.setSenderAccount(account2.getAccountNumber());
        transferDto.setSum("500");

        response = client.target(String.format("http://localhost:%s/transfers", SUPPORT.getLocalPort()))
                .request()
                .post(Entity.json(transferDto));

        assertEquals(response.getStatus(), 500);

        client.target(String.format("http://localhost:%s/accounts/%s", SUPPORT.getLocalPort(), account1.getAccountNumber()))
                .request()
                .delete();

        client.target(String.format("http://localhost:%s/accounts/%s", SUPPORT.getLocalPort(), account2.getAccountNumber()))
                .request()
                .delete();
    }

    private AccountDto getAccount(String holderName, String accountNumber, String amount) {
        return new AccountDto().setHolderName(holderName).setAccountNumber(accountNumber).setAmount(amount);
    }

}
