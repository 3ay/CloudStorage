package ru.netology.cloudstorage.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import ru.netology.cloudstorage.security.JwtTokenFilter;
import ru.netology.cloudstorage.security.TokenStore;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Value("${server.cors.originFromHeader.label}")
    private String corsOriginFromHeaderLabel;

    @Value("#{'${server.cors.allowedOrigins}'.split(';')}")
    private List<String> corsAllowedOrigins;

    @Value("#{'${server.cors.allowedMethods}'.split(';')}")
    private List<String> corsAllowedMethods;


    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(tokenStore);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
http
        .csrf().disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)
        .and()
        .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests()
        .antMatchers("/cloud/login").permitAll()
        .anyRequest().authenticated()
        .and()
        .cors().configurationSource(this::getCorsConfiguration)
        .and()
        .csrf().disable().headers().frameOptions().disable();

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    private CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration cc = new CorsConfiguration().applyPermitDefaultValues();
        cc.setAllowCredentials(true);
        for (String corsAllowedMethod : corsAllowedMethods) {
            cc.addAllowedMethod(HttpMethod.valueOf(corsAllowedMethod));
        }
        List<String> list = new ArrayList<>();
        for (String s : corsAllowedOrigins) {

            String originH = request.getHeader("Origin");

            if (originH == null) {
                continue;
            }

            String origin = s.replace(corsOriginFromHeaderLabel, request.getHeader("Origin"));

            list.add(origin);
        }
        cc.setAllowedOrigins(list);
        return cc;
    }

}
