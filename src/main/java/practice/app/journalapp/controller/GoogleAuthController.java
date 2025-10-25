package practice.app.journalapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.repository.UserRepository;
import practice.app.journalapp.service.GoogleAuthService;
import practice.app.journalapp.service.UserDetailsServiceImpl;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {
    @Autowired
    GoogleAuthService googleAuthService;


    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code){
        try {
            String jwtToken = googleAuthService.processAuthorizationCode(code);
            return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("token",jwtToken));
        }
        catch (Exception exception){
            log.error("Error Accured Oauth2");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
