package com.academichub.academic_management_hub.security;

import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
   private final UserRepository userRepository;

   @Override
   @Transactional(readOnly = true)
   public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
       return new JwtUserDetails(user);
   }

   @Transactional(readOnly = true)
   public UserDetails loadUserById(UUID id) throws UsernameNotFoundException {
       User user = userRepository.findById(id)
               .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
       return new JwtUserDetails(user);
   }
}