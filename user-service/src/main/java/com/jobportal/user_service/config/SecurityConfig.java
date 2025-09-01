package com.jobportal.user_service.config;


import com.jobportal.user_service.Services.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {

    private  final UserDetailServiceImpl userDetailService;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request.
                        requestMatchers("/user/**").authenticated()
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        .anyRequest().permitAll())
                        .csrf(AbstractHttpConfigurer::disable)
                        .build();
    }


    @Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}

