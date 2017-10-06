package me.amberize.moneytransfer;

import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import me.amberize.moneytransfer.health.AppHealthCheck;
import me.amberize.moneytransfer.resource.AccountsResource;
import me.amberize.moneytransfer.resource.TransfersResource;
import org.apache.ignite.Ignition;

public class App extends io.dropwizard.Application<AppConfig> {

    public static void main(String[] args) throws Exception {
        Ignition.start();

        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        GuiceBundle<AppConfig> guiceBundle = GuiceBundle.<AppConfig>newBuilder()
                .addModule(new AppModule())
                .enableAutoConfig(getClass().getPackage().getName())
                .setConfigClass(AppConfig.class)
                .build();

        bootstrap.addBundle(guiceBundle);
    }

    public void run(AppConfig appConfig, Environment environment) throws Exception {
//        environment.jersey().register(TransfersResource.class);
//        environment.jersey().register(AccountsResource.class);
//
//        environment.healthChecks().register("app", new AppHealthCheck());
    }

    @Override
    public String getName() {
        return "Money transfer";
    }
}
