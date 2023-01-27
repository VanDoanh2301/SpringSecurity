package com.example.demo_jwt.config.security;

import com.example.demo_jwt.config.jwt.JwtEntryPoint;
import com.example.demo_jwt.config.jwt.JwtTokenFilter;
import com.example.demo_jwt.config.jwt.JwtTokenFilterHeader;
import com.example.demo_jwt.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class WebSecurityConfig {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;
    @Autowired
    private JwtTokenFilterHeader jwtTokenFilterHeader;
    @Autowired
    private JwtEntryPoint jwtEntryPoint;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailServiceImpl();
    }
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(encoder());

        return authProvider;
    }
    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ADMIN > USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                  .authorizeRequests().requestMatchers("/login").permitAll()
//                  .requestMatchers("/api/users").permitAll()
//                  .requestMatchers("/api/auth/users").hasAnyAuthority("ADMIN","USER")
//                  .requestMatchers("/api/auth/admin").hasAuthority("ADMIN")
//
//
//                .requestMatchers(HttpMethod.GET,"/api/users").hasAnyAuthority("ADMIN","USER")
//                .requestMatchers(HttpMethod.POST,"/api/signup").hasAnyAuthority("ADMIN")
//                .requestMatchers(HttpMethod.DELETE,"/api/remove/{id}").hasAnyAuthority("ADMIN")
               .requestMatchers("/api/**").hasAnyAuthority("ADMIN","USER")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/404")
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter(encoder().encode("password"))
                .and()
                .httpBasic()

        ;
        http.exceptionHandling().authenticationEntryPoint(jwtEntryPoint);
        //http.exceptionHandling().accessDeniedHandler(new AccessDeniedExceptionHandler());
        http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(HttpStatus.FORBIDDEN.value()));
        http.exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtTokenFilterHeader, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
