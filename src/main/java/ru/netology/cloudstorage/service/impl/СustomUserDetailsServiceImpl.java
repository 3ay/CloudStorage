package ru.netology.cloudstorage.service.impl;

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
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<UserDetailsDAO> optionalUserDetailsDAO = userCredentialsRepository.findByLogin(login);
        if (optionalUserDetailsDAO.isPresent()) {
            return new User(optionalUserDetailsDAO.get().getLogin(),
                    optionalUserDetailsDAO.get().getPassword(),
                    getAuthorities(optionalUserDetailsDAO.get()));
        } else
            throw new UsernameNotFoundException("User not found with login: " + login);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserDetailsDAO user) {
        return Arrays.stream(user.getAuthorities().split(","))
                .map(String::trim)
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }
}
