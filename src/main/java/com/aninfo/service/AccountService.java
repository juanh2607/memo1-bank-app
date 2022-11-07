package com.aninfo.service;

import com.aninfo.model.Account;
import com.aninfo.repository.AccountRepository;
import com.aninfo.exceptions.InvalidTransactionTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionService transactionService;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long cbu) {
        return accountRepository.findById(cbu);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void deleteById(Long cbu) {
        accountRepository.deleteById(cbu);
    }

    @Transactional
    public Account withdraw(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);
        if (account == null) {
            throw new InvalidTransactionTypeException("La cuenta a retirar no existe");
        }
        transactionService.withdraw(account, sum);
        account.setBalance(account.getBalance() - sum);
        accountRepository.save(account);
        return account;
    }

    @Transactional
    public Account deposit(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);
        if (account == null) {
            throw new InvalidTransactionTypeException("La cuenta a depositar no existe");
        }
        Double deposited = transactionService.deposit(account, sum);
        account.setBalance(account.getBalance() + deposited);
        accountRepository.save(account);
        return account;
    }

}
