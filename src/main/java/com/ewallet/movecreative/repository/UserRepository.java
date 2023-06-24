package com.ewallet.movecreative.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ewallet.movecreative.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByPhoneNumber(String phoneNumber);

    User findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

}
