package com.github.alexeylapin.whaleone.infrastructure.security;

import lombok.Getter;
import lombok.experimental.Delegate;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserDetailsIdUser implements UserDetails, IdUser {

    @Getter
    private final long id;

    @Delegate
    private final UserDetails userDetails;

    public UserDetailsIdUser(long id, UserDetails userDetails) {
        this.userDetails = userDetails;
        this.id = id;
    }

    @Override
    public String getName() {
        return userDetails.getUsername();
    }

    @Override
    public Object getDelegate() {
        return userDetails;
    }

}
