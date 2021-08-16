package com.luxon.assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Account {

    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "account")
    private List<Balance> balances;

    @OneToMany(mappedBy = "account")
    private List<Wallet> wallets;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public List<Wallet> getWallets() {
        return wallets;
    }

    public void setWallets(List<Wallet> wallets) {
        this.wallets = wallets;
    }

    public Balance getBalanceForInstrument(Instrument instrument) {
        this.balances.stream()
                .filter(balance -> instrument.equals(balance.getInstrument()))
                .findFirst()
                .orElse(null));
    }

    public Wallet getPreferredWallet(){
        return this.wallets.stream().findFirst();
    }
}
