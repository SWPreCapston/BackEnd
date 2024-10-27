//package com.precapston.precapston.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() { // 암호화 메소드
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//
//        http
//                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers("/", "/login", "/loginProc", "/join", "/joinProc").permitAll()
//                        .requestMatchers("/admin").hasRole("ADMIN")
//                        .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER")
//                        .anyRequest().authenticated()
//                ); // 경로에 대한 인가 작업
//
//
//        http
//                .formLogin((auth) -> auth.loginPage("/login")
//                        .loginProcessingUrl("/loginProc")
//                        .permitAll()
//                ); // 로그인 페이지에 대한 자동작업
//
//
//        http
//                .logout((auth) -> auth.logoutUrl("/logout")
//                        .logoutSuccessUrl("/")); // 로그아웃
//
//
////        http
////                .csrf((auth) -> auth.disable()); // csrf 설정 작업
//
//
//        http
//                .sessionManagement((auth) -> auth
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(true)); // 다중 로그인 설정 -> 세션 통제
//
//
//        http
//                .sessionManagement((auth) -> auth
//                        .sessionFixation().changeSessionId()); // 세션 고정 보호
//
//
//        return http.build();
//    }
//}
package com.precapston.precapston.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { // 암호화 메소드
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login", "/loginProc", "/join", "/joinProc").permitAll() // 모든 사용자 허용
                        .requestMatchers("/admin").hasRole("ADMIN") // ADMIN 역할만 접근 허용
                        .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER") // ADMIN, USER 역할 접근 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger 접근 허용
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                );

        http
                .formLogin((auth) -> auth.loginPage("/login")
                        .loginProcessingUrl("/loginProc")
                        .permitAll()
                );

        http
                .logout((auth) -> auth.logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                );

        http
                .sessionManagement((session) -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true) // 다중 로그인 방지
                );

        http
                .sessionManagement((session) -> session
                        .sessionFixation().migrateSession() // 세션 고정 보호
                );

        return http.build();
    }
}

