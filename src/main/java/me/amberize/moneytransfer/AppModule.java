package me.amberize.moneytransfer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import me.amberize.moneytransfer.domain.Account;
import me.amberize.moneytransfer.domain.Transfer;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {}

    @Provides
    public MapperFacade mapperFacade() {
        return new DefaultMapperFactory.Builder().build().getMapperFacade();
    }

    @Provides
    public Ignite ignite() {
        Ignite ignite = Ignition.ignite();

        CacheConfiguration<String, Account> accountsCacheCfg = new CacheConfiguration<>(Account.class.getName());
        accountsCacheCfg.setCacheMode(CacheMode.PARTITIONED);
        accountsCacheCfg.setIndexedTypes(String.class, Account.class);
        ignite.getOrCreateCache(accountsCacheCfg);

        CacheConfiguration<String, Account> transfersCacheCfg = new CacheConfiguration<>(Transfer.class.getName());
        accountsCacheCfg.setCacheMode(CacheMode.PARTITIONED);
        ignite.getOrCreateCache(transfersCacheCfg);

        return ignite;
    }

}
