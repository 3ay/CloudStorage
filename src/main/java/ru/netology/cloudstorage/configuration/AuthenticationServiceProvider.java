package ru.netology.cloudstorage.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationServiceProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
      String login = authentication.getName();
      String password = authentication.getCredentials().toString();
      UserDetails userDetails = userDetailsService.loadUserByUsername(login);
        if (userDetails != null && password.equals(userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(login, password, userDetails.getAuthorities());
        }
        throw new BadCredentialsException("Invalid login or password");
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
