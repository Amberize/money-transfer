package me.amberize.moneytransfer.repository;

import lombok.extern.slf4j.Slf4j;
import me.amberize.moneytransfer.domain.Account;
import me.amberize.moneytransfer.exceptions.StorageException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionException;

import javax.cache.Cache;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class AccountsRepository {

    public static final String CACHE_NAME = Account.class.getName();

    private IgniteCache<String, Account> cache;

    @Inject
    private Ignite ignite;

    @Inject
    public AccountsRepository(Ignite ignite) {
        cache = ignite.cache(CACHE_NAME);
    }

    public void create(Account account) throws StorageException {
        try(Transaction tx = ignite.transactions().txStart()) {
            cache.put(account.getAccountNumber(), account);

            tx.commit();
        } catch (TransactionException e) {
            log.error(e.getMessage(), e);

            throw new StorageException(e.getMessage(), e);
        }
    }

    public List<Account> getAll() {
        QueryCursor<Cache.Entry<String, Account>> cursor = cache.query(new SqlQuery<String, Account>(Account.class, "deleted = 0"));

        return cursor.getAll().stream().map(Cache.Entry::getValue).collect(Collectors.toList());
    }

    public Account get(String accountNumber) throws StorageException {
        Account account;

        try(Transaction tx = ignite.transactions().txStart()) {
            account = cache.get(accountNumber);

            tx.commit();
        } catch (TransactionException e) {
            log.error(e.getMessage(), e);

            throw new StorageException(e.getMessage(), e);
        }

        return account;
    }

    public void update(Account account) throws StorageException {
        try(Transaction tx = ignite.transactions().txStart()) {
            cache.put(account.getAccountNumber(), account);

            tx.commit();
        } catch (TransactionException e) {
            log.error(e.getMessage(), e);

            throw new StorageException(e.getMessage(), e);
        }
    }

}
