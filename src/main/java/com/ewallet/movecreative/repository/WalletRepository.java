package com.ewallet.movecreative.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ewallet.movecreative.entity.User;
import com.ewallet.movecreative.entity.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>{

    Wallet findByUser(User user);

    @Query(value = "SELECT * FROM wallets WHERE user_id = :userId", nativeQuery = true )
    Wallet findByUserId(Long userId);
}
