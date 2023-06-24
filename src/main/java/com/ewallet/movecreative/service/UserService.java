package com.ewallet.movecreative.service;

import java.util.Collections;

import javax.validation.Valid;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ewallet.movecreative.dto.UserDTO;
import com.ewallet.movecreative.entity.User;
import com.ewallet.movecreative.entity.Wallet;
import com.ewallet.movecreative.repository.UserRepository;
import com.ewallet.movecreative.repository.WalletRepository;

import javassist.NotFoundException;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, WalletRepository walletRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException{
        User user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null){
            throw new UsernameNotFoundException("User not found");
        }

        return new CustomUserDetails(user.getId(), user.getPhoneNumber(), user.getPassword(), Collections.emptyList());
    }

    public User createUser(@Valid UserDTO userDTO) {

        if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is already registered");
        }
        
        User user = new User();
        user.setPhoneNumber(userDTO.getPhoneNumber());

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        user.setPassword(encodedPassword);
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());

        Wallet wallet = new Wallet();
        wallet.setBalance(0);
        wallet.setUser(user);

        System.out.println(user);

        return userRepository.save(user);
    }

    public Wallet getWalletByUser(String phoneNumber) throws NotFoundException{
        User user = userRepository.findByPhoneNumber(phoneNumber);
        if(user == null){
            throw new NotFoundException("User not found");
        }

        return walletRepository.findByUser(user);
    }

    public Authentication authenticateUser(String phoneNumber, String password){
        UserDetails userDetails = loadUserByUsername(phoneNumber);
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid username or password");
        }
        
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
