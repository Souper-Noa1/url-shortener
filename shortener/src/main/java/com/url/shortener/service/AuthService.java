package com.url.shortener.service;

import com.url.shortener.config.AppConfig;
import com.url.shortener.domain.dtos.request.LoginRequest;
import com.url.shortener.domain.dtos.request.RegisterRequest;
import com.url.shortener.domain.dtos.response.AuthResponse;
import com.url.shortener.domain.entities.User;
import com.url.shortener.exception.InvalidUrlException;
import com.url.shortener.repository.UserRepository;
import com.url.shortener.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AppConfig appConfig;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       AppConfig appConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.appConfig = appConfig;
    }


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new InvalidUrlException("USERNAME_TAKEN", "Username '" + request.username() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new InvalidUrlException("EMAIL_TAKEN", "Email '" + request.email() + "' is already registered");
        }

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password())
        );
        userRepository.save(user);

        log.info("Registered new user: {}", request.username());

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, "Bearer", user.getUsername(), appConfig.getJwt().getExpirationMs());
    }


    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        log.info("User logged in: {}", request.username());

        String token = jwtService.generateToken(request.username());
        return new AuthResponse(token, "Bearer", request.username(), appConfig.getJwt().getExpirationMs());
    }
}

