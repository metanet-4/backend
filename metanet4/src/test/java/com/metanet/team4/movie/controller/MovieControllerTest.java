package com.metanet.team4.movie.controller;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.team4.common.LoginResponseDto;
import com.metanet.team4.common.TestDataUtils;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.movie.service.IMovieService;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MovieControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private IMovieService movieService;
	
	@Autowired
	private TestDataUtils testDataUtils;
	
	private String token;
	private Member member;
	private String userid;
	private String movieId;
	private String imageUrl;
	private String keyword;
	
	@BeforeEach
	void beforeEach() {
		LoginResponseDto loginResponseDto = testDataUtils.getLoginAccessToken();
    	token = loginResponseDto.getToken();
    	member = loginResponseDto.getMember();
    	movieId="20223819";
    	imageUrl = "http://file.koreafilm.or.kr/poster/99/18/70/DPF030543_01.jpg";
    	keyword = "비밀";
	}
	
	@Test
	void movieDetailTest() throws Exception {
		mockMvc.perform(get("/movie/detail/{movieId}", movieId)
				.cookie(new Cookie("jwt", token)))
				.andExpect(status().isOk());
	}

	@Test
    void toggleLike() throws Exception {
        String movieId = "20248530";
        String userId = member.getUserId();

        // 1️⃣ 좋아요를 누르기 전에는 isLiked가 false여야 함
        when(movieService.isLiked(userId, movieId)).thenReturn(false);

        // 2️⃣ 좋아요 추가 요청
        mockMvc.perform(post("/movie/detail/{movieId}", movieId)
                .cookie(new Cookie("jwt", token)))
                .andExpect(status().isOk());

        // 좋아요가 추가되었으므로 다음 호출에서는 isLiked가 true여야 함
        when(movieService.isLiked(userId, movieId)).thenReturn(true);

        // 3️⃣ 좋아요 취소 요청
        mockMvc.perform(post("/movie/detail/{movieId}", movieId)
                .cookie(new Cookie("jwt", token)))
                .andExpect(status().isOk());

        // 좋아요가 취소되었으므로 다시 isLiked는 false여야 함
        when(movieService.isLiked(userId, movieId)).thenReturn(false);

        // `isLiked()`가 실제로 호출되었는지 검증
        verify(movieService, atLeastOnce()).isLiked(userId, movieId);
    }
	
	@Test
    void proxyImageTest() throws Exception {
        // GET 요청 수행 및 검증
        mockMvc.perform(get("/movie/proxy-image").param("url", imageUrl)
        	.cookie(new Cookie("jwt", token)))        
        	.andExpect(status().isOk()); // HTTP 200 응답 확인
    }
	
	@Test
    void getLikedMoviesTest() throws Exception {
        mockMvc.perform(get("/movie/likeList")
                .cookie(new Cookie("jwt", token))) // JWT 토큰 포함
                .andExpect(status().isOk()); // HTTP 200 응답 확인
	}

	@Test
    void getBoxOfficeMoviesTest() throws Exception {
        mockMvc.perform(get("/movie/boxoffice")
                .cookie(new Cookie("jwt", token))) // JWT 토큰 포함
                .andExpect(status().isOk()); // HTTP 200 응답 확인
	}
	
	@Test
    void getComingSoonMoviesTest() throws Exception {
        mockMvc.perform(get("/movie/comingsoon")
                .cookie(new Cookie("jwt", token))) // JWT 토큰 포함
                .andExpect(status().isOk()); // HTTP 200 응답 확인
	}
	
	@Test
    void getSearchMoviesTest() throws Exception {
        mockMvc.perform(get("/movie/search/{keyword}", keyword)
                .cookie(new Cookie("jwt", token))) // JWT 토큰 포함
                .andExpect(status().isOk()); // HTTP 200 응답 확인
	}
	
	@Test
    void getSearchMoviesCountTest() throws Exception {
        mockMvc.perform(get("/movie/search/{keyword}/count", keyword)
                .cookie(new Cookie("jwt", token))) // JWT 토큰 포함
                .andExpect(status().isOk()); // HTTP 200 응답 확인
	}
	
}
