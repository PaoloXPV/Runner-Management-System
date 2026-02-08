package com.ElVacio.runner.config; // O el paquete donde la crees

import com.ElVacio.runner.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Password encoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        // Public endpoints
                        .requestMatchers("/registro", "/registrarse", "/css/**", "/js/**").permitAll()
                        // Protected endpoints
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    // User authentication using database
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepo) {
        return username -> {
            var usuario = usuarioRepo.findByUsername(username);

            if (usuario == null) {
                throw new UsernameNotFoundException("User not found");
            }

            return User.builder()
                    .username(usuario.getUsername())
                    .password(usuario.getPassword())
                    .roles("USER")
                    .build();
        };
    }
}