package me.amberize.moneytransfer.health;

import com.hubspot.dropwizard.guice.InjectableHealthCheck;
import org.apache.ignite.Ignite;

import javax.inject.Inject;


public class IgniteHealthCheck extends InjectableHealthCheck {

    @Inject
    private Ignite ignite;

    @Override
    public String getName() {
        return "ignite";
    }

    @Override
    protected Result check() throws Exception {
        if(ignite.active()) {
            return Result.healthy();
        }

        return Result.unhealthy("Ignite is not active");
    }
}
