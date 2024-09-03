package rs.ac.bg.fon.authenticationservice.config;

import rs.ac.bg.fon.authenticationservice.filter.CsrfCookieFilter;
import rs.ac.bg.fon.authenticationservice.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfig corsConfig;
    private final LogoutHandler accountLogoutHandler;

    @Value("${ignored-paths.post}")
    private String[] ignoredPostPaths;

    @Value("${ignored-paths.delete}")
    private String[] ignoredDeletePaths;

    @Value("${ignored-paths.get}")
    private String[] ignoredGetPaths;

    @Value("${ignored-paths.put}")
    private String[] ignoredPutPaths;

    @Value("${ignored-paths.csrf}")
    private String[] ignoredCsrfPaths;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http.csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers(ignoredCsrfPaths).ignoringRequestMatchers(ignoredPutPaths)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST, ignoredPostPaths).permitAll()
                        .requestMatchers(HttpMethod.DELETE, ignoredDeletePaths).permitAll()
                        .requestMatchers(HttpMethod.GET, ignoredGetPaths).permitAll()
                        .requestMatchers(HttpMethod.PUT, ignoredPutPaths).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, LogoutFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler(accountLogoutHandler)
                        .logoutSuccessHandler(((request, response, authentication) ->
                                SecurityContextHolder.clearContext()
                        ))
                );
        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(corsConfig.getAllowedMethods());
        config.setAllowCredentials(corsConfig.isAllowedCredentials());
        config.setAllowedHeaders(corsConfig.getAllowedHeaders());
        config.setExposedHeaders(corsConfig.getExposedHeaders());
        config.setMaxAge(corsConfig.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
