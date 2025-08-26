package com.boratok.Config;

import com.boratok.exception.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class
SecurityConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint customEntryPoint) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> request
                        // Admin erişimi
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // User erişimi
                        .requestMatchers("/user/**").hasRole("USER")
                        // Ortak erişim (kayıt, giriş)
                        .requestMatchers("/rest/api/route/update/complate-delivery/**").hasRole("driver")
                        .requestMatchers("/rest/api/route/**").hasRole("admin")
                        .requestMatchers("/register", "/login").permitAll()
                        .requestMatchers("/rest/api/car/**").hasRole("admin")
                        //.requestMatchers("/rest/api/route/update/complate-delivery/**").authenticated()
                        // Diğer her şey giriş yapmış olmalı
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(customEntryPoint))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(10));//db de şifreli olarak saklandığı için eşlerken normal haliyle eşlemeyi hedefler
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //protected void configure(AuthenticationManagerBuilder auth) throw Exception;
}
