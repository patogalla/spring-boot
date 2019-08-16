package com.patogalla.api.user.security;

import com.patogalla.api.user.service.TokenService;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private TokenService tokenService;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.antMatcher("/**/*")
                .cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and()
                .authorizeRequests()
                .antMatchers("/*","/**/*", "/").permitAll()
                .anyRequest().authenticated()
                .and()
                .anonymous()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedEntryPoint());
        http.addFilterBefore(new AuthenticationFilter(authenticationManager(), unauthorizedEntryPoint()), BasicAuthenticationFilter.class);
        http.addFilterBefore(new LoggerFilter(),AuthenticationFilter.class);
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                //Registration
                "/public/**/*",
                "/public/*",
                "/v2/*",
                "/v2/**/*",
                "/webjars/*",
                "/webjars/**/*",
                "/csrf",
                "/csrf/*",
                "/csrf/**/*",
                "/swagger-resources",
                "/swagger-resources/*",
                "/swagger-resources/**/*",
                "/swagger-ui.html",
                "/swagger-ui.html/**/*",
                "/swagger-ui.html/*")
                .antMatchers(HttpMethod.OPTIONS, "/**/*", "/*");
    }

    @Bean
    AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        };
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(tokenAuthenticationProvider());
    }

    @Bean
    AuthenticationProvider tokenAuthenticationProvider() {
        return new TokenAuthenticationProvider(tokenService);
    }

    @Bean
    public SecurityContextHolderAwareRequestFilter securityContextHolderAwareRequestFilter() {
        return new SecurityContextHolderAwareRequestFilter();
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(ImmutableList.of("*"));
        configuration.setAllowedMethods(ImmutableList.of("GET", "POST", "PATCH", "OPTIONS", "DELETE", "PUT"));
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**/*", configuration);
        source.registerCorsConfiguration("/*", configuration);
        return source;
    }
}
