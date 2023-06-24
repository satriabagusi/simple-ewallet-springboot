package com.ewallet.movecreative.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ewallet.movecreative.exception.InsufficientBalanceException;
import com.ewallet.movecreative.exception.NotFoundException;
import com.ewallet.movecreative.service.TransactionService;


@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@RequestParam Long senderId, @RequestParam Long recipientId, @RequestParam double amount) {
        
        Map<String, Object> responseMsg = new HashMap<>();

        try {
            
            transactionService.transferMoney(senderId, recipientId, amount);

            responseMsg.put("code", HttpStatus.ACCEPTED.toString());
            responseMsg.put("message", "Transfer successfully");

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseMsg);
        } catch (InsufficientBalanceException e) {

            responseMsg.put("code", HttpStatus.NOT_ACCEPTABLE.toString());
            responseMsg.put("message", "Insufficient balance");

            return ResponseEntity.badRequest().body(responseMsg);
        } catch (NotFoundException e) {

            responseMsg.put("code", HttpStatus.NOT_FOUND.toString());
            responseMsg.put("message", "User or Wallet not found.");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMsg);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/top-up")
    public ResponseEntity<?> topUpWallet(@RequestParam Long userId, @RequestParam double amount) {
        
        Map<String, Object> responseMsg = new HashMap<>();

        try {
            transactionService.topUpWallet(userId, amount);

            responseMsg.put("code", HttpStatus.ACCEPTED.toString());
            responseMsg.put("message", "Top up successfully.");

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseMsg);
        } catch (NotFoundException e) {
            responseMsg.put("code", HttpStatus.NOT_FOUND.toString());
            responseMsg.put("message", "User or Wallet not found.");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMsg);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawFromWallet(@RequestParam Long userId, @RequestParam double amount) {
        
        Map<String, Object> responseMsg = new HashMap<>();

        try {
            transactionService.withdrawFromWallet(userId, amount);

            responseMsg.put("code", HttpStatus.ACCEPTED.toString());
            responseMsg.put("message", "Withdraw Balance successfully.");

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseMsg);
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body("Insufficient balance");
        } catch (NotFoundException e) {
            responseMsg.put("code", HttpStatus.NOT_FOUND.toString());
            responseMsg.put("message", "User or Wallet not found.");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMsg);
        }
    }
    
}
