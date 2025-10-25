package practice.app.journalapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import practice.app.journalapp.service.UserDetailsServiceImpl;
import practice.app.journalapp.util.JwtUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
public class  GoogleAuthService {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;


    public String processAuthorizationCode(String code) {
        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("code", code);
        param.add("client_id", clientId);
        param.add("client_secret", clientSecret);
        param.add("redirect_uri", "https://developers.google.com/oauthplayground");
        param.add("grant_type", "authorization_code");
        param.add("code", code);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, header);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
        String idToken = (String) tokenResponse.getBody().get("id_token");
        String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
        UserDetails userDetails = null;
        String jwtToken = null;
        if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> userInfo = userInfoResponse.getBody();
            String email = (String) userInfo.get("email");
            try {
                userDetails = userDetailsService.loadUserByUsername(email);
             }
            catch (Exception e) {
                User user = new User();
                user.setEmail(email);
                user.setUsername(email);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setRole(Arrays.asList("USER"));
                userRepository.save(user);
            }

            UsernamePasswordAuthenticationToken authentication  =
                    new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwtToken = jwtUtil.generateToken(email);
        }
            return jwtToken;

    }
}