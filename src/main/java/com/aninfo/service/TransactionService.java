package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.service.AccountService;
import com.aninfo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class TransactionService {
    private static final double MIN_DEPOSITO_PROMOCIONABLE = 2000;
    private static final int PORCENTAJE_EXTRA_PROMO = 10;
    private static final double LIMITE_PROMO = 500;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;

    public Transaction createDeposit(Transaction transaction) {
        Account account = transaction.getAccount();
        accountService.deposit(account.getCbu(), transaction.getAmount());
        //return transactionRepository.save(transaction);
        return transaction;
    }

    public Transaction createWithdraw(Transaction transaction) {
        Account account = transaction.getAccount();
        accountService.withdraw(account.getCbu(), transaction.getAmount());
        //return transactionRepository.save(transaction);
        return transaction;
    }

    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    public Collection<Transaction> getAccountTransactions(Long cbu) {
        return transactionRepository.findTransactionsByAccount_Cbu(cbu);
    }

    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }

    @Transactional
    public void withdraw(Account account, Double amount) {
        // Regla de Negocio: No permitir extracciones mayores al saldo de la cuenta
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        Transaction transaction = new Transaction(account, -amount);
        account.addTransaction(transaction);
        transactionRepository.save(transaction);
    }

    // Retorna el valor real depositado luego de revisar promociones aplicables
    @Transactional
    public Double deposit(Account account, Double amount) {
        // Regla de Negocio: No permitir depósitos de montos nulos o negativos
        if (amount <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }
        // Bank account promo, get 10% extra in your $2000+ deposits, up to $500
        Double extra = (double) 0;
        if (amount >= MIN_DEPOSITO_PROMOCIONABLE) {
            extra = (double) PORCENTAJE_EXTRA_PROMO * amount / 100;
            if (extra > LIMITE_PROMO)
                extra = LIMITE_PROMO;
        }
        Transaction transaction = new Transaction(account, amount + extra);
        account.addTransaction(transaction);
        transactionRepository.save(transaction);

        return amount + extra;
    }

}
