package com.ewallet.movecreative.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ewallet.movecreative.dto.LoginRequestDTO;
import com.ewallet.movecreative.dto.UserDTO;
import com.ewallet.movecreative.entity.User;
import com.ewallet.movecreative.service.CustomUserDetails;
import com.ewallet.movecreative.service.UserService;
import com.ewallet.movecreative.util.JwtTokenUtil;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private UserService userService;
    private JwtTokenUtil jwtTokenUtil;

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil){
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult){
        Map<String, Object> responseMsg = new HashMap<>();

        if(bindingResult.hasErrors()){
            List<String> errorMsg = bindingResult.getAllErrors().stream()
                                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                    .collect(Collectors.toList());
            
            responseMsg.put("message", errorMsg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMsg);
        }

        User createdUser = userService.createUser(userDTO);
        responseMsg.put("code", HttpStatus.CREATED.toString());
        responseMsg.put("message", "Register successfully");
        responseMsg.put("data", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMsg);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest){

        Map<String, String> responseMsg = new HashMap<>();

        try {
            Authentication authentication = userService.authenticateUser(loginRequest.getPhoneNumber(), loginRequest.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            responseMsg.put("code", HttpStatus.ACCEPTED.toString());
            responseMsg.put("message", "Login success");
            responseMsg.put("data", token);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseMsg);
        } catch (AuthenticationException e) {
            responseMsg.put("code", HttpStatus.UNAUTHORIZED.toString());
            responseMsg.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMsg);
        }
    }
}
