package com.widescope.sqlThunder.config;

import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig implements WebMvcConfigurer  {

    @Autowired
    private AppConstants appConstants;

    private static final String[] AUTH_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        if( appConstants.getIsSwagger() ) {
            /*In order for swagger to work enable the line below */
            http.authorizeHttpRequests((auth) -> {
                auth.requestMatchers("/swagger-ui*/**").permitAll();
                auth.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll();
                auth.anyRequest().authenticated();
            }
            ).httpBasic(Customizer.withDefaults());
        }


        http.csrf(AbstractHttpConfigurer::disable); /*this is a workaround to accept all end points with all verbs*/
        return http.build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new BasicAuthInterceptor());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService inMemoryUserDetailsManager() {
        UserDetails user = User.builder()
                .username(SpringSecurityWrapper.username)
                .password(passwordEncoder().encode(SpringSecurityWrapper.userPassword))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username(SpringSecurityWrapper.adminName)
                .password(passwordEncoder().encode(SpringSecurityWrapper.adminPassword))
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }



}