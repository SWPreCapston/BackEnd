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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { // 암호화 메소드
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 최신 방식의 CORS 설정
                .csrf(csrf -> csrf.disable()) // 최신 방식의 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // 최신 방식의 권한 설정

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080")); // 허용할 Origin
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // React 앱의 주소로 수정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

