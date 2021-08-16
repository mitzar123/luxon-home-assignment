package com.luxon.assignment.service;

import com.luxon.assignment.dto.ExchangeRequestDto;
import com.luxon.assignment.entity.Account;
import com.luxon.assignment.entity.Balance;
import com.luxon.assignment.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final AccountRepository accountRepository;

    private final WalletRepository walletRepository;

    private final RateRepository rateRepository;


    public ResponseEntity<?> execute(ExchangeRequestDto exchangeRequestDto) {

        if(ExchangeType.BUY.equals(exchangeRequestDto.getExchangeType())){
            executeBuy(exchangeRequestDto);
        } else if (ExchangeType.SELL.equals(exchangeRequestDto.getExchangeType())){
            executeSell(exchangeRequestDto);
        } else {
            executeSend(exchangeRequestDto);
        }

        //TODO -- add logic here
        return ResponseEntity.ok(200);
    }

    private void executeBuy(ExchangeRequestDto exchangeRequestDto) {
        Account account = accountRepository.get(exchangeRequestDto.getAccountId());
        Wallet accountWallet = walletRepository.get(exchangeRequestDto.getWalletId())

        Balance accountBalanceForInstrument = account.getBalanceForInstrument(accountWallet.getInstrument());

        if( Objects.isNull(accountBalanceForInstrument) ) {
            throw new NoSuchBalancetException("Account {} does not have ballance for instrument {} ",
                    exchangeRequestDto.getAccountId(), accountWallet.getInstrument() );
        }

        Double convertedInstrumentAmountToAcquire = rateRepository.convert(accountWallet.getInstrument(),
                exchangeRequestDto.getInstrument(),
                exchangeRequestDto.getAmount());

        if (accountBalanceForInstrument.getQty() > convertedInstrumentAmountToAcquire) {
            buyForAccount(exchangeRequestDto.getInstrument(), account, accountBalanceForInstrument, convertedInstrumentAmountToAcquire);
        }

    }

    private void buyForAccount(Instrument instrument, Account account, Balance accountBalanceForInstrument, Double convertedInstrumentAmountToAcquire) {
        accountBalanceForInstrument.setQty(accountBalanceForInstrument.getQty() - convertedInstrumentAmountToAcquire);

        Balance accountBalanceForBuyInstrument = account.getBalanceForInstrument(exchangeRequestDto.getInstrument());
        if( Objects.isNull(accountBalanceForBuyInstrument) ) {
            // I assume I want make a new wallet for new currency if he does not have one
            account.addWallet(Wallet.builder()
                            .instrument(instrument)
                            .walletAdress(instrument)
                            .account(account)
                    )
            account.addBalance(Balance.builder()
                            .instrument(instrument)
                            .qty(convertedInstrumentAmountToAcquire)
                            .account(account))
        } else {
            accountBalanceForBuyInstrument.getQty() += convertedInstrumentAmountToAcquire;
            account.set(accountBalanceForBuyInstrument);
        }
    }

    private void executeSell(ExchangeRequestDto exchangeRequestDto) {
        Account account = accountRepository.get(exchangeRequestDto.getAccountId());
        Wallet accountWallet = walletRepository.get(exchangeRequestDto.getWalletId())
        Balance accountBalanceForInstrument = account.getBalanceForInstrument(accountWallet.getInstrument());

        if (accountBalanceForInstrument.getQty() > exchangeRequestDto.getAmount()) {

            Double convertedInstrumentAmountToGainFromSell = rateRepository.convert(
                    exchangeRequestDto.getInstrument(),
                    accountWallet.getInstrument(),
                    exchangeRequestDto.getAmount());


            accountBalanceForInstrument.setQty( accountBalanceForInstrument.getQty() - exchangeRequestDto.getAmount() );

            Balance accountBalanceForSellInstrument = account.getBalanceForInstrument(accountWallet.getInstrument());
            if( Objects.isNull(accountBalanceForSellInstrument) ) {
                throw new NoSuchBalancetException("Account {} does not have ballance for instrument {} ",
                        exchangeRequestDto.getAccountId(), accountWallet.getInstrument() );
            } else {
                accountBalanceForSellInstrument.getQty() += convertedInstrumentAmountToGainFromSell;
                account.set(accountBalanceForSellInstrument);
            }

        }

    }

    private void executeSend(ExchangeRequestDto exchangeRequestDto) {
        if(Objects.isNull(exchangeRequestDto.getAccountDestinationId())) {
            throw new NullPointerException("Destination account ID not set");
        }

        Account account = accountRepository.get(exchangeRequestDto.getAccountId());
        Balance accountBalanceForInstrument = account.getBalanceForInstrument(accountWallet.getInstrument());

        // I am assuming the amount sent to friend is in the wallet that is being used currency
        if (accountBalanceForInstrument.getQty() > exchangeRequestDto.getAmount()) {
            accountBalanceForInstrument.setQty( accountBalanceForInstrument.getQty() - exchangeRequestDto.getAmount());

            Account destinationAccount = accountRepository.get(exchangeRequestDto.getAccountDestinationId());
            Balance destinationAccountBalanceForInstrument = account.getBalanceForInstrument(accountWallet.getInstrument());
            if( Objects.isNull(destinationAccountBalanceForInstrument) ) {

                Double convertAmountToGet = rateRepository.convert(accountWallet.getInstrument(),
                        destinationAccount.getPreferredWallet().getInstrument(),
                        exchangeRequestDto.getAmount());

                Balance destinationBalance = destinationAccount.getBalanceForInstrument(
                                                        destinationAccount.getPreferredWallet().getInstrument());

                destinationBalance.setQty(destinationBalance.getQty() + convertAmountToGet);
            } else {
                destinationAccountBalanceForInstrument.setQty(destinationAccountBalanceForInstrument.getQty() + convertAmountToGet);
            }
        }

    }


    @PostConstruct
    public void doSome() {
        Account account = accountRepository.save(Account.builder().id(1).name("Jack Black").build());
        List<Balance> userBalance = Collections.singletonList(Balance.builder().account(account).build());
        account.setBalances(userBalance);
    }
}
