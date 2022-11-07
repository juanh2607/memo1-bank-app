package com.aninfo.model;

import javax.persistence.*;
import com.aninfo.model.Account;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double amount;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Transaction() {}

    public Transaction(Account account, Double amount) {
        this.amount = amount;
        this.account = account;
    }

    public Double getAmount(){
        return amount;
    }

    public Account getAccount(){
        return account;
    }
}
