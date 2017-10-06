package me.amberize.moneytransfer.repository;

import lombok.extern.slf4j.Slf4j;
import me.amberize.moneytransfer.domain.Transfer;
import me.amberize.moneytransfer.exceptions.StorageException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Slf4j
@Singleton
public class TransfersRepository {

    public static final String CACHE_NAME = Transfer.class.getName();

    private IgniteCache<String, Transfer> cache;

    @Inject
    private Ignite ignite;

    @Inject
    public TransfersRepository(Ignite ignite) {
        cache = ignite.cache(CACHE_NAME);
    }

    public Transfer create(Transfer transfer) throws StorageException {
        try(Transaction tx = ignite.transactions().txStart()) {
            transfer.setId(UUID.randomUUID().toString());

            cache.put(transfer.getId(), transfer);

            tx.commit();
        } catch (TransactionException e) {
            log.error(e.getMessage(), e);

            throw new StorageException(e.getMessage(), e);
        }

        return transfer;
    }

}
