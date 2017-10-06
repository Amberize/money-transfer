package me.amberize.moneytransfer.services;

import lombok.extern.slf4j.Slf4j;
import me.amberize.moneytransfer.domain.Account;
import me.amberize.moneytransfer.domain.Transfer;
import me.amberize.moneytransfer.exceptions.StorageException;
import me.amberize.moneytransfer.exceptions.TransferException;
import me.amberize.moneytransfer.repository.AccountsRepository;
import me.amberize.moneytransfer.repository.TransfersRepository;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

@Slf4j
@Singleton
public class TransferService {

    private IgniteCache<String, Account> accountsCache;

    private IgniteCache<String, Transfer> transfersCache;

    private static ThreadLocal<DecimalFormat> decimalFormat = ThreadLocal.withInitial(() -> new DecimalFormat("#.##"));

    @Inject
    private Ignite ignite;

    @Inject
    public TransferService(Ignite ignite) {
        accountsCache = ignite.cache(AccountsRepository.CACHE_NAME);
        transfersCache = ignite.cache(TransfersRepository.CACHE_NAME);
    }

    public void executeTransfer(String transferId) throws StorageException, TransferException {
        try(Transaction tx = ignite.transactions().txStart()) {
            Transfer transfer = transfersCache.get(transferId);

            BigDecimal transferSum;
            try {
                decimalFormat.get().setParseBigDecimal(true);
                transferSum = (BigDecimal) decimalFormat.get().parse(transfer.getSum());
            } catch (ParseException e) {
                tx.rollback();

                throw new TransferException(e.getMessage());
            }

            Account sender = accountsCache.get(transfer.getSenderAccount());
            Account recipient = accountsCache.get(transfer.getRecipientAccount());

            BigDecimal senderAmount = sender.getAmount();
            if(senderAmount.compareTo(transferSum) < 0) {
                tx.rollback();

                throw new TransferException("Sender doesn't have enough money for transaction");
            }

            sender.setAmount(sender.getAmount().subtract(transferSum));

            accountsCache.put(sender.getAccountNumber(), sender);

            recipient.setAmount(recipient.getAmount().add(transferSum));

            accountsCache.put(recipient.getAccountNumber(), recipient);

            tx.commit();
        } catch (TransactionException e) {
            log.error(e.getMessage(), e);

            throw new StorageException(e.getMessage(), e);
        }
    }

}
