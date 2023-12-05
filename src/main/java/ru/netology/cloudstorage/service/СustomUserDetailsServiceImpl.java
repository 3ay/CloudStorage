package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.dao.UserDetailsDAO;
import ru.netology.cloudstorage.repository.UserCredentialsRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class СustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserCredentialsRepository userCredentialsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserDetailsDAO> optionalUserDetailsDAO = userCredentialsRepository.findByUsername(username);
        if (optionalUserDetailsDAO.isPresent()) {
            return new User(optionalUserDetailsDAO.get().getUsername(),
                    optionalUserDetailsDAO.get().getPassword(),
                    getAuthorities(optionalUserDetailsDAO.get()));
        } else
            throw new UsernameNotFoundException("User not found with username: " + username);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserDetailsDAO user) {
        return Arrays.stream(user.getAuthorities().split(","))
                .map(String::trim)
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }
}
