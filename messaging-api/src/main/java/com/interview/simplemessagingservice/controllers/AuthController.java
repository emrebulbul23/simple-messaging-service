package com.interview.simplemessagingservice.controllers;

import com.interview.simplemessagingservice.model.SimpleUser;
import com.interview.simplemessagingservice.repositories.IUserRepository;
import com.interview.simplemessagingservice.response.JwtResponse;
import com.interview.simplemessagingservice.security.jwt.JwtUtils;
import com.interview.simplemessagingservice.util.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
     * Sign up new user to the system. If user already exists in db, returns 404.
     *
     * @param username Username
     * @param password Password
     * @return ResponseEntity<String>
     */
    @Operation(summary = "Create user")
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestParam(name = "username", required = true) String username,
                                               @RequestParam(name = "password", required = true) String password) {
        final SimpleUser userReq = new SimpleUser(username, password);
        boolean isExist = userRepository.existsByUsername(userReq.getUsername());
        if (isExist) {
            logger.error(MessageFormat.format("User already exists, cannot create user! Username: {0}",
                    username));
            return ResponseEntity.status(400).body("User already exists");
        }

        // Create new user's account
        SimpleUser user = new SimpleUser(userReq.getUsername(),
                encoder.encode(userReq.getPassword()));

        userRepository.save(user);

        logger.info(MessageFormat.format("User registered successfully! Username: {0}",
                username));
        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * Sign in existing user to the system. If user does not exist, returns 404.
     *
     * @param username Username
     * @param password Password
     * @return {@link ResponseEntity}
     */
    @Operation(summary = "Authenticate user")
    @PostMapping("/signin")
    public ResponseEntity authenticateUser(@RequestParam(name = "username") String username,
                                           @RequestParam(name = "password") String password) {
        final SimpleUser userReq = new SimpleUser(username, password);
        boolean isExist = userRepository.existsByUsername(userReq.getUsername());
        if (!isExist) {
            logger.error(MessageFormat.format("User to be authenticated cannot be found! Username: {0}",
                    userReq.getUsername()));
            return ResponseEntity.status(400).body("User cannot be found");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userReq.getUsername(), userReq.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        logger.info(MessageFormat.format("User authenticated successfully Username: {0}",
                userReq.getUsername()));
        //get user id
        SimpleUser byUsername = userRepository.findByUsername(username);
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername(),
                byUsername.getId()));
    }

    /**
     * Block user. If user to be blocked exists, it is added to the blocked
     * users list of the user.
     *
     * @param username Name of the user to be blocked.
     * @return ResponseEntity<String>
     */
    @Operation(summary = "Block user", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/blockUser/{username}")
    public ResponseEntity<String> blockUser(@PathVariable String username) {
        // check if user to be blocked exists
        boolean exists = userRepository.existsByUsername(username);
        if (!exists) {
            logger.error(MessageFormat.format("User to be blocked cannot be found! Username: {0}",
                    username));
            return ResponseEntity.status(404).body("User to be blocked cannot be found");
        }

        String authenticatedUsersUsername = CommonUtil.getInstance().getAuthenticatedUsersUsername();
        SimpleUser byUsername = userRepository.findByUsername(authenticatedUsersUsername);
        // check if user already blocked
        if (byUsername.getBlockedUsers().contains(username)) {
            logger.error(MessageFormat.format("User is already blocked! Username: {0}",
                    username));
            return ResponseEntity.status(400).body("User is already blocked!");
        }

        byUsername.addBlockedUser(username);
        userRepository.save(byUsername);

        String format = MessageFormat.format("{0} blocked successfully by {1}",
                username, byUsername.getUsername());
        logger.info(format);
        return ResponseEntity.ok(format);
    }

    /**
     * Get the list of blocked users blocked by the authenticated user.
     *
     * @return List of blocked users.
     */
    @Operation(summary = "Get blocked users", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/blockedUsers")
    public ResponseEntity<List<String>> getBlockedUsersList() {
        String authenticatedUsersUsername = CommonUtil.getInstance().getAuthenticatedUsersUsername();
        SimpleUser byUsername = userRepository.findByUsername(authenticatedUsersUsername);
        return ResponseEntity.ok(byUsername.getBlockedUsers());
    }
}
