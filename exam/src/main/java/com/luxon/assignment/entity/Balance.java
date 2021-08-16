package com.luxon.assignment.entity;

import com.luxon.assignment.enums.Instrument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Balance {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    private Instrument instrument;

    @Column
    private Double qty;

    @ManyToOne
    private Account account;

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
