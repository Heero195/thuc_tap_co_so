package com.example.demo.controller;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.config.JwtUtils;
import com.example.demo.entity.User;
import com.example.demo.model.dto.AuthRes;
import com.example.demo.model.dto.UserDto;

import com.example.demo.model.request.CreateUserReq;
import com.example.demo.model.request.LoginReq;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Operation(summary = "Login to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login success returning JWT token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthRes> authenticateUser(@Valid @RequestBody LoginReq loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(loginRequest.getEmail());

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        AuthRes res = new AuthRes(jwt, userService.getUserById(user.getId()));

        return ResponseEntity.ok(res);
    }

    @Operation(summary = "Register a new user account")
    @PostMapping("/register")
    public ResponseEntity<AuthRes> registerUser(@Valid @RequestBody CreateUserReq req) {

        UserDto createdUserDto = userService.createUser(req);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(req.getEmail());

        return ResponseEntity.ok(new AuthRes(jwt, createdUserDto));
    }
}
