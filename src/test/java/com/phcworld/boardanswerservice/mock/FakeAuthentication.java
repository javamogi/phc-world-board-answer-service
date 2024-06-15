package com.phcworld.boardanswerservice.mock;

import com.phcworld.boardanswerservice.domain.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor
public class FakeAuthentication {

    private final String userId;
    private final String password;
    private final Authority authority;

    public Authentication getAuthentication() throws UsernameNotFoundException {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(authority.toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal =  new User(
                userId,
                password,
                authorities
        );

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
