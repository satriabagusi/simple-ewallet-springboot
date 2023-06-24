package com.ewallet.movecreative.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ewallet.movecreative.entity.Wallet;
import com.ewallet.movecreative.exception.InsufficientBalanceException;
import com.ewallet.movecreative.exception.NotFoundException;
import com.ewallet.movecreative.repository.WalletRepository;



@Service
public class TransactionService {
    
    private WalletRepository walletRepository;

    public TransactionService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void transferMoney(Long senderId, Long recipientId, double amount) throws InsufficientBalanceException{
        Wallet fromWallet = walletRepository.findByUserId(senderId);
        Wallet toWallet = walletRepository.findByUserId(recipientId);

        if(fromWallet == null || toWallet == null){
            throw new NotFoundException("User or Wallet not found");
        }

        double senderBalance = fromWallet.getBalance();
        if(senderBalance < amount){
            throw new InsufficientBalanceException("Insufficient balance");
        }

        fromWallet.setBalance(senderBalance - amount);
        toWallet.setBalance(toWallet.getBalance() + amount);
    }

    @Transactional
    public void withdrawFromWallet(Long userId, double amount) throws NotFoundException, InsufficientBalanceException {
        Wallet wallet = walletRepository.findByUserId(userId);

        if (wallet == null) {
            throw new NotFoundException("User or Wallet not found");
        }

        double balance = wallet.getBalance();
        if (balance < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        wallet.setBalance(balance - amount);
    }

    @Transactional
    public void topUpWallet(Long userId, double amount) throws NotFoundException {
        Wallet wallet = walletRepository.findByUserId(userId);

        if (wallet == null) {
            throw new NotFoundException("User or Wallet not found");
        }

        wallet.setBalance(wallet.getBalance() + amount);
    }
}
