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
import practice.app.journalapp.service.UserDetailsServiceImpl;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String clientId ;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String clientSecret;
    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code){
        try {
            String tokenEndpoint = "https://oauth2.googleapis.com";

              MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            param.add("code",code);
            param.add("client_id",clientId);
            param.add("client_secret",clientSecret);
            param.add("redirect_uri","https://developers.google.com/oauthplayground");
            param.add("grant_type","authorization_code");
            param.add("code",code);

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(param,header);
            ResponseEntity<Map> tokenResponse =  restTemplate.postForEntity(tokenEndpoint,request,Map.class);
            String idToken = (String) tokenResponse.getBody().get("id_token");
            String userInfoUrl =  "https://oauth2.googleapis.com/tokeninfo?id_token="+idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl,Map.class);
            if(userInfoResponse.getStatusCode()== HttpStatus.OK){
                Map<String,Object> userInfo = userInfoResponse.getBody();
                String email  = (String) userInfo.get("email");
                UserDetails userDetails= null ;
                try {
                    userDetails = userDetailsService.loadUserByUsername(email);
                }
                catch (Exception e){
                    User user = new User();
                    user.setEmail(email);
                    user.setUsername(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRole(Arrays.asList("USER"));
                    userRepository.save(user);
                    userDetails = userDetailsService.loadUserByUsername(email);
                }

                UsernamePasswordAuthenticationToken authentication  =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return
                        ResponseEntity.status(HttpStatus.OK).build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
        catch (Exception exception){
            log.error("Error Accured Oauth2");
                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
