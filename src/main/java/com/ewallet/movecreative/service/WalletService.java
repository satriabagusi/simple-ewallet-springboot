package com.ewallet.movecreative.service;

import com.ewallet.movecreative.entity.User;
import com.ewallet.movecreative.entity.Wallet;
import com.ewallet.movecreative.repository.UserRepository;
import com.ewallet.movecreative.repository.WalletRepository;

import javassist.NotFoundException;

public class WalletService {
    private WalletRepository walletRepository;
    private UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository){
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public Wallet updateBalance(String phoneNumber, double amount){
        try {
            User user = userRepository.findByPhoneNumber(phoneNumber);
            if (user == null) {
                throw new NotFoundException("User not found");
            }

            Wallet wallet = walletRepository.findByUser(user);
            if (wallet == null) {
                throw new NotFoundException("Wallet not found");
            }

            double newBalance = wallet.getBalance() + amount;
            wallet.setBalance(newBalance);

            walletRepository.save(wallet);

            return wallet;
        } catch (NotFoundException e) {
            throw new RuntimeException("User or wallet not found", e);
        }
    }
}
