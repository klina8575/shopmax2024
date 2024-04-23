package com.shopmax.controller;

import com.shopmax.dto.MemberFormDto;
import com.shopmax.entity.Member;
import com.shopmax.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc //mockMvc 테스트를 위해 어노테이션 선언
@TestPropertySource(locations="classpath:application-test.properties")
public class MemberControllerTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc; //모형 객체 => 웹 브라우저에서 request하는것 처럼 테스트 할수 있는 객체

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(String email, String password){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(email);
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword(password);
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        return memberService.saveMember(member);
    }


    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception {
        String email = "test@gmail.com";
        String password = "1234";
        createMember(email, password);

        mockMvc.perform(SecurityMockMvcRequestBuilders
                        .formLogin().userParameter("email")
                        .loginProcessingUrl("/members/login") //로그인 처리할 경로
                        .user(email).password(password))
                .andExpect(SecurityMockMvcResultMatchers.authenticated()); //로그인이 성공하면 테스트 코드를 통과시킴
    }


    @Test
    @DisplayName("로그인 실패 테스트")
    public void loginFailTest() throws Exception {
        String email = "test@gmail.com";
        String password = "1234";
        createMember(email, password);

        mockMvc.perform(SecurityMockMvcRequestBuilders
                        .formLogin().userParameter("email")
                        .loginProcessingUrl("/members/login") //로그인 처리할 경로
                        .user(email).password("12345")) //비밀번호 일부러 다르게 입력
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated()); //로그인이 실패하면 테스트 코드를 통과시킴
    }

}
