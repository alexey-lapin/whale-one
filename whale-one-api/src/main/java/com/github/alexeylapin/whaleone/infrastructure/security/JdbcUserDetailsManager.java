package com.github.alexeylapin.whaleone.infrastructure.security;

import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.UserEntity;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.UserJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.List;

@RequiredArgsConstructor
public class JdbcUserDetailsManager implements UserDetailsManager {

    private final UserJdbcRepository repository;

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .map(JdbcUserDetailsManager::getIdUser)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private static UserDetailsIdUser getIdUser(UserEntity it) {
        List<SimpleGrantedAuthority> authorities = it.getAuthorities().stream()
                .map(i -> new SimpleGrantedAuthority(i.name()))
                .toList();
        User userDetails = new User(it.getUsername(),
                it.getPassword(),
                it.isEnabled(),
                true,
                true,
                true,
                authorities);
        return new UserDetailsIdUser(it.getId(), userDetails);
    }

}
