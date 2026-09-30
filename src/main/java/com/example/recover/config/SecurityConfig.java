package com.example.recover.config;

import com.example.recover.vo.Result;
import com.example.recover.utils.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    private final ObjectMapper objectMapper;

    private static final String[] WHITELIST = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            // Vue 前端
            "/",
            "/index.html",
            "/assets/**",
            "/login",
            "/m/**",
            "/receiving-orders/**",
            "/expiry-records/**",
            "/suppliers/**",
            "/supplier-products/**",
            // 上传图片，允许未登录访问
            "/uploads/damage/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 无状态
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITELIST).permitAll()
                        // 管理员才能注册新用户
                        .requestMatchers("/api/auth/register").hasRole("ADMIN")
                        // 放行登录注册接口
                        .requestMatchers("/api/auth/**").permitAll()
                        // 收货单：ADMIN + STAFF
                        .requestMatchers("/api/orders/**")
                        .hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/items/**")
                        .hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/damages/**")
                        .hasAnyRole("ADMIN", "STAFF")
                        // 有效期：ADMIN + STAFF
                        .requestMatchers("/api/records/**")
                        .hasAnyRole("ADMIN", "STAFF")
                        // 供应商：查询可以给 STAFF，增删改只有 ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**")
                        .hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/suppliers/**")
                        .hasRole("ADMIN")
                        // 供应商商品：ADMIN
                        .requestMatchers("/api/codes/**")
                        .hasRole("ADMIN")
                        // 商品管理：ADMIN
                        .requestMatchers("/api/products/**")
                        .hasRole("ADMIN")
                        // 用户管理：ADMIN
                        .requestMatchers("/api/user/**")
                        .hasRole("ADMIN")
                        // 其余接口需要登录
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) -> {
                            log.info("authenticationEntryPoint 拦截路径: {}", request.getRequestURI());

                            response.setStatus(401);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    objectMapper.writeValueAsString(Result.fail(401, "请先登录")));
                        })
                        .accessDeniedHandler((request, response, e) -> {
                            log.info("accessDeniedHandler 拦截路径: {}", request.getRequestURI());
                            response.setStatus(403);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    objectMapper.writeValueAsString(Result.fail(403, "无访问权限")));
                        }))
                // JWT 过滤器加在 Security 过滤器之前
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 密码加密
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}