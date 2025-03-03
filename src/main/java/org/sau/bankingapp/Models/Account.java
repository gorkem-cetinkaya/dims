package org.sau.bankingapp.Models;

import java.math.BigDecimal;

public class Account {
    private int id;
    private String branch;
    private BigDecimal balance;
    private int customerId;

    public Account() {
    }

    public Account(int id, String branch, BigDecimal balance, int customerId) {
        this.id = id;
        this.branch = branch;
        this.balance = balance;
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "Account{id=" + id + ", branch='" + branch + "', balance=" + balance + ", customerId=" + customerId + "}";
    }
}
