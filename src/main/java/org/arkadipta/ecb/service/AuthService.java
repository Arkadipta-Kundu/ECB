package org.arkadipta.ecb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arkadipta.ecb.dto.auth.JwtResponse;
import org.arkadipta.ecb.dto.auth.LoginRequest;
import org.arkadipta.ecb.dto.auth.RefreshTokenRequest;
import org.arkadipta.ecb.dto.auth.SignupRequest;
import org.arkadipta.ecb.exception.UserAlreadyExistsException;
import org.arkadipta.ecb.model.Cart;
import org.arkadipta.ecb.model.User;
import org.arkadipta.ecb.model.enums.Role;
import org.arkadipta.ecb.repository.CartRepository;
import org.arkadipta.ecb.repository.UserRepository;
import org.arkadipta.ecb.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtResponse register(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email is already taken!");
        }

        // Create new user
        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        // Create cart for the user
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        // Generate tokens
        String jwt = jwtUtils.generateToken(savedUser);
        String refreshToken = jwtUtils.generateRefreshToken(savedUser);

        return new JwtResponse(jwt, refreshToken, savedUser.getEmail(),
                savedUser.getName(), savedUser.getRole().name());
    }

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        return new JwtResponse(jwt, refreshToken, user.getEmail(),
                user.getName(), user.getRole().name());
    }

    public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (jwtUtils.validateJwtToken(refreshToken)) {
            String userEmail = jwtUtils.extractUsername(refreshToken);
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newAccessToken = jwtUtils.generateToken(user);
            String newRefreshToken = jwtUtils.generateRefreshToken(user);

            return new JwtResponse(newAccessToken, newRefreshToken, user.getEmail(),
                    user.getName(), user.getRole().name());
        } else {
            throw new RuntimeException("Invalid refresh token");
        }
    }
}