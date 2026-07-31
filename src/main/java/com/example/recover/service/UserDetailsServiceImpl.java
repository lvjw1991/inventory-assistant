package com.example.recover.service;

import com.example.recover.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.recover.entity.User user = userRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())  // ADMIN / CASHIER / WAREHOUSE
                .accountExpired(false)
                .accountLocked(!user.getEnabled())
                .build();
    }
}
