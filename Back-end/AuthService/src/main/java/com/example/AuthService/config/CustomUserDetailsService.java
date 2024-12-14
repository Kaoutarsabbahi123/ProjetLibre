package com.example.AuthService.config;

import com.example.AuthService.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //String userServiceUrl = "http://gateway-service:8222/users/email/" + email;
        String userServiceUrl = "http://localhost:8222/users/email/" + email;
        UserResponse userResponse = restTemplate.getForObject(userServiceUrl, UserResponse.class);

        if (userResponse == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
        }

        // Conversion en CustomUserDetails
        return new CustomUserDetails(userResponse);
    }
}
