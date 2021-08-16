package com.luxon.assignment.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ExchangeRequestDto {

    enum ExchangeType {
        BUY,SELL,SEND
    }

    @NotNull
    private Integer accountId;

    @NotNull
    private Integer walletId;

    @NotNull
    private ExchangeType exchangeType;

    @NotNull
    private Instrument instrument;

    @NotNull
    private Double amount;

    private Integer accountDestinationId;


    //TODO - add more relevant fields here for some generic request


    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getWalletId() {
        return walletId;
    }

    public void setWalletId(Integer walletId) {
        this.walletId = walletId;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getAccountDestinationId() {
        return accountDestinationId;
    }

    public void setAccountDestinationId(Integer accountDestinationId) {
        this.accountDestinationId = accountDestinationId;
    }
}
