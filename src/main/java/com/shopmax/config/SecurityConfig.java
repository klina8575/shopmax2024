package com.shopmax.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration //Bean 객체를 싱글톤으로 공유할 수 있게 해준다.
@EnableWebSecurity //spring security filterChain이 자동으로 포함되게 한다
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		//람다로 변경됨
		   
		http.authorizeHttpRequests(authorize -> authorize //1. 페이지 접근에 관한 설정
				//모든 사용자가 로그인(인증) 없이 접근할 수 있도록 설정
				.requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/fonts/**").permitAll()
				.requestMatchers("/", "/members/**", "/item/**").permitAll()
				.requestMatchers("/favicon.ico", "/error").permitAll()
				//'admin'으로 시작하는 경로는 관리자만 접근가능하도록 설정
				.requestMatchers("/admin/**").hasRole("ADMIN")
				//그 외의 페이지는 모두 로그인(인증을 받아야 한다)
				.anyRequest().authenticated() 
				) 
		.formLogin(formLogin -> formLogin //2. 로그인에 관련된 설정
				.loginPage("/members/login") //로그인 페이지 URL 설정
				.defaultSuccessUrl("/") //로그인 성공시 이동할 페이지
				.usernameParameter("email") //로그인시 id로 사용할 파라메터 이름
				.failureUrl("/members/login/error") //로그인 실패시 이동할 URL
				) 
		.logout(logout -> logout //3. 로그아웃에 관련된 설정
				.logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) //로그아웃시 이동할 URL
				.logoutSuccessUrl("/") //로그아웃 성공시 이동할 URL
				)   //4.인증되지 않은 사용자가 리소스에 접근했을때 설정(ex. 로그인 안했는데 cart페이지에 접근..)
		.exceptionHandling(handling -> handling
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				)
		.rememberMe(Customizer.withDefaults());
		   
		
	    return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
