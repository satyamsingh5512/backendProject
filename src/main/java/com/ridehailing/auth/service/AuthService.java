package com.ridehailing.auth.service;

import com.ridehailing.auth.domain.User;
import com.ridehailing.auth.domain.UserRole;
import com.ridehailing.auth.dto.AuthResponse;
import com.ridehailing.auth.dto.LoginRequest;
import com.ridehailing.auth.dto.SignupRequest;
import com.ridehailing.auth.repository.UserRepository;
import com.ridehailing.common.exception.BusinessException;
import com.ridehailing.common.security.CustomUserDetails;
import com.ridehailing.driver.domain.Driver;
import com.ridehailing.driver.repository.DriverRepository;
import com.ridehailing.rider.domain.Rider;
import com.ridehailing.rider.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        log.info("Signup request for email: {}, role: {}", request.getEmail(), request.getRole());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        
        user = userRepository.save(user);
        log.info("User created with id: {}", user.getId());

        // Create role-specific profile
        if (request.getRole() == UserRole.RIDER) {
            Rider rider = Rider.builder()
                    .userId(user.getId())
                    .name(request.getName())
                    .phoneNumber(request.getPhoneNumber())
                    .build();
            riderRepository.save(rider);
            log.info("Rider profile created for user: {}", user.getId());
        } else if (request.getRole() == UserRole.DRIVER) {
            Driver driver = Driver.builder()
                    .userId(user.getId())
                    .name(request.getName())
                    .phoneNumber(request.getPhoneNumber())
                    .build();
            driverRepository.save(driver);
            log.info("Driver profile created for user: {}", user.getId());
        }

        String token = jwtService.generateToken(new CustomUserDetails(user));

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .userId(user.getId())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        String token = jwtService.generateToken(new CustomUserDetails(user));

        log.info("Login successful for user: {}", user.getId());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .userId(user.getId())
                .build();
    }
}
