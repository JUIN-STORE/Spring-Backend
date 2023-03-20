package store.juin.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import store.juin.api.domain.enums.AccountRole;
import store.juin.api.jwt.ForbiddenHandler;
import store.juin.api.jwt.TokenAuthenticationEntryPoint;
import store.juin.api.jwt.TokenRequestFilter;

// https://gaemi606.tistory.com/entry/Spring-Boot-JWT%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EB%8A%94-Spring-Security-%EB%A1%9C%EA%B7%B8%EC%9D%B8-REST-API


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final TokenRequestFilter tokenRequestFilter;
    private final TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;

    private final ForbiddenHandler forbiddenHandler;

    @Value("${front.url}")
    private String frontUrl;

    /**
     * 비밀번호 해시
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 인증방법
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 시큐리티가 무시
     *
     * @param webSecurity
     */
    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers(
                "/css/**",
                "/js/**",
                "/img/**",
                "/lib/**",
                "/v3/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui/**",
                "/webjars/**",
                "/docs/**",
                "/index.html"
        );
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 개발 시에만 사용
        httpSecurity.csrf().disable();

        // security 기본 /login 페이지 사용 안 함.
        httpSecurity.httpBasic().disable();

        final String[] whitelist = new String[]{
                "/api/accounts/refresh",
                "/api/accounts/sign-in",
                "/api/accounts/sign-up",
                "/api/accounts/mail",
                "/api/accounts/password",
                "/api/accounts/duplication/**",
                "/api/items",
                "/api/items/*/**",
                "/api/items/count",
                "/api/items/search/**",
                "/api/items/search/count",
                "/api/categories",
                "/api/tokens/re-issue",
                "/api/authorizes/**"
        };

        httpSecurity
                .authorizeRequests()

                // role이 admin만 해당 엔드포인트 접근 가능
                .antMatchers("/api/admin/**").hasRole(AccountRole.ADMIN.name()) // admin만 허용

                // 같은 role일 때 read, write 등등 다르게 권한 줄 때 사용
                // .antMatchers("/api/admin/**").hasAuthority(AccountRole.ADMIN.name())

                .antMatchers(whitelist).permitAll() // whitelist 전체 접근 허용
//                .antMatchers("/check").hasAnyRole("ADMIN", "USER") // ROLE_ADMIN 혹은 ROLE_USER 권한을 가진 사용자만 접근 허용

                // 나머지 요청은 모두 인증 필요
                .anyRequest().authenticated()
                .and().formLogin().disable()

                .headers().frameOptions().disable()

                .and().cors();

        // JWT 설정
        httpSecurity
                // 인증되지 않은 사용자 접근 시
                .exceptionHandling()
                .authenticationEntryPoint(tokenAuthenticationEntryPoint)
                .accessDeniedHandler(forbiddenHandler)

                // 세션을 사용하지 않기 때문에 STATELESS 설정
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // JWT Filter 적용
                .and().addFilterBefore(tokenRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // front url
        configuration.addAllowedOrigin(frontUrl);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

