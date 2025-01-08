package com.github.alexeylapin.whaleone.infrastructure.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class IdUser extends User {

    private final long id;

    public IdUser(long id,
                  String username,
                  String password,
                  boolean enabled,
                  Collection<? extends GrantedAuthority> authorities) {
        super(username,
                password,
                enabled,
                true,
                true,
                true,
                authorities);
        this.id = id;
    }

}
