package carga.demo.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
            auth.requestMatchers(
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/register"),
                    new AntPathRequestMatcher("/guardarCliente"),
                    new AntPathRequestMatcher("/public/**"),
                    new AntPathRequestMatcher("/resources/**"),
                    new AntPathRequestMatcher("/static/**"),
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**")
            ).permitAll();
            auth.anyRequest().authenticated();
        }).formLogin(form -> {
            form.loginPage("/login");
            form.defaultSuccessUrl("/", true);
            form.permitAll();
        }).logout(LogoutConfigurer::permitAll);

        return http.build();
    }
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder)
            throws Exception {
        var authenticationManagerBuilder = new DaoAuthenticationProvider();
        authenticationManagerBuilder.setUserDetailsService(userDetailsService);
        authenticationManagerBuilder.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationManagerBuilder);
    }
}