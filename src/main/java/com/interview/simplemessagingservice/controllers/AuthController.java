package com.interview.simplemessagingservice.controllers;

import com.interview.simplemessagingservice.model.SimpleUser;
import com.interview.simplemessagingservice.repositories.IUserRepository;
import com.interview.simplemessagingservice.response.JwtResponse;
import com.interview.simplemessagingservice.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    /*
     * Autowired AuthenticationManager.
     */
    @Autowired
    AuthenticationManager authenticationManager;

    /*
     * Autowired IUserRepository collection to db.
     */
    @Autowired
    IUserRepository userRepository;

    /*
     * Autowired PasswordEncoder.
     */
    @Autowired
    PasswordEncoder encoder;

    /*
     * Autowired JwtUtils.
     */
    @Autowired
    JwtUtils jwtUtils;

    /**
     * Sign up new user to the system.  If user already exists in db, returns 404.
     * @param userReq SimpleUser object containing username and password.
     * @return ResponseEntity
     */
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody SimpleUser userReq) {
        boolean isExist = userRepository.existsByUsername(userReq.getUsername());
        if (isExist) {
            return ResponseEntity.status(400).body("User already exists");
        }

        // Create new user's account
        SimpleUser user = new SimpleUser(userReq.getUsername(),
                encoder.encode(userReq.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * Sign in existing user to the system. If user does not exist, returns 404.
     * @param userReq SimpleUser object containing username and password.
     * @return ResponseEntity
     */
    @PostMapping("/signin")
    public ResponseEntity authenticateUser(@RequestBody SimpleUser userReq) {
        boolean isExist = userRepository.existsByUsername(userReq.getUsername());
        if (!isExist) {
            return ResponseEntity.status(400).body("User cannot be found");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userReq.getUsername(), userReq.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername()));
    }
}
