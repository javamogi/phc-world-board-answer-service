package com.phcworld.boardanswerservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.boardanswerservice.domain.Authority;
import com.phcworld.boardanswerservice.dto.FreeBoardAnswerRequestDto;
import com.phcworld.boardanswerservice.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FreeBoardAnswerApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;

    @BeforeEach
    void 토큰_생성(){
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_ADMIN.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("a2240b59-47f6-4ad4-ba07-f7c495909f40", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);
        token = "Bearer " + accessToken;
    }

    @Test
    void 답변_등록_성공() throws Exception {
        FreeBoardAnswerRequestDto requestDto = FreeBoardAnswerRequestDto.builder()
                .boardId(1L)
                .contents("contents")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);

        this.mvc.perform(post("/freeboards/answers")
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 답변_등록_실패_없는_게시물() throws Exception {
        FreeBoardAnswerRequestDto requestDto = FreeBoardAnswerRequestDto.builder()
                .boardId(1000L)
                .contents("contents")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);

        this.mvc.perform(post("/freeboards/answers")
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 답변_하나_조회() throws Exception {

        this.mvc.perform(get("/freeboards/answers/{id}", 1L)
                        .header("Authorization", token)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 답변_하나_조회_없는_답변() throws Exception {

        this.mvc.perform(get("/freeboards/answers/{id}", 2L)
                        .header("Authorization", token)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 답변_수정_성공() throws Exception {
        FreeBoardAnswerRequestDto requestDto = FreeBoardAnswerRequestDto.builder()
                .answerId(1L)
                .contents("contents 수정")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);

        this.mvc.perform(patch("/freeboards/answers")
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 답변_수정_실패_없는_답변() throws Exception {
        FreeBoardAnswerRequestDto requestDto = FreeBoardAnswerRequestDto.builder()
                .answerId(2L)
                .contents("contents 수정")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);

        this.mvc.perform(patch("/freeboards/answers")
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 답변_수정_실패_수정_권한_없음() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_USER.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("2", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = "Bearer " + tokenProvider.generateAccessToken(authentication, now);

        FreeBoardAnswerRequestDto requestDto = FreeBoardAnswerRequestDto.builder()
                .answerId(1L)
                .contents("contents 수정")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);

        this.mvc.perform(patch("/freeboards/answers")
                        .header("Authorization", accessToken)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 답변_삭제_성공() throws Exception {
        this.mvc.perform(delete("/freeboards/answers/{id}", 1L)
                        .header("Authorization", token)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 답변_삭제_실패_없는_답변() throws Exception {
        this.mvc.perform(delete("/freeboards/answers/{id}", 2L)
                        .header("Authorization", token)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 답변_삭제_실패_권한_없음() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_USER.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("2", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = "Bearer " + tokenProvider.generateAccessToken(authentication, now);

        this.mvc.perform(delete("/freeboards/answers/{id}", 1L)
                        .header("Authorization", accessToken)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}