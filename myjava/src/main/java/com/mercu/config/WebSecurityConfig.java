package com.mercu.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import com.mercu.member.service.MemberUserDetailsService;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@EnableWebSecurity
@ComponentScan(basePackages = {"com.mercu"})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MemberUserDetailsService memberUserDetailsService;

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring().antMatchers("/static/**");
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
            .authorizeRequests()
//                .antMatchers("/bl").hasRole("ADMIN")
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/**").permitAll()
            .and()
            .logout().logoutSuccessUrl("/bl")
            .and()
            .formLogin().defaultSuccessUrl("/bl")
            .and()
            .exceptionHandling()
            .accessDeniedPage("/accessDenied")
//            .accessDeniedHandler(accessDeniedHandler())
//            .authenticationEntryPoint(authenticationEntryPoint())
            ;
//                .httpBasic().realmName("mercu")
//                .authenticationEntryPoint(memberBasicAuthenticationEntryPoint())
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        AuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        return entryPoint;
    }

    private AccessDeniedHandler accessDeniedHandler() {
        AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                System.out.println("access deinied");
            }
        };
        return  accessDeniedHandler;
    }

    @Bean
    public BasicAuthenticationEntryPoint memberBasicAuthenticationEntryPoint() {
        return new MemberBasicAuthenticationEntryPoint();
    }

    public class MemberBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("HTTP Status 401 - " + authException.getMessage());
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            setRealmName("mercu");
            super.afterPropertiesSet();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder)  throws Exception {
        authenticationManagerBuilder.userDetailsService(memberUserDetailsService).passwordEncoder(passwordEncoder());
    }
}
