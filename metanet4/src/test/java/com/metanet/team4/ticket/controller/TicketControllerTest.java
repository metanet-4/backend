package com.metanet.team4.ticket.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.team4.common.LoginResponseDto;
import com.metanet.team4.common.TestDataUtils;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.ticket.dto.TicketRequestDto;

import jakarta.servlet.http.Cookie;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private TestDataUtils testDataUtils;
    
    private String token;
    private Member member;
    
    @BeforeEach
    void beforeEach() {
    	LoginResponseDto loginResponseDto = testDataUtils.getLoginAccessToken();
    	token = loginResponseDto.getToken();
    	member = loginResponseDto.getMember();
    }
	
    @Test
    @DisplayName("영화관 조회 테스트")
    void testGetCinemaList() throws Exception {

    	String movieId = "20223819";
    
    	mockMvc.perform(get("/ticket/cinema").param("movieId", movieId))
    			.andExpect(status().isOk());
    }
    
    
	@Test
	@DisplayName("상영관 조회 테스트")
	void testGetPlayingList() throws Exception {
		Long cinemaId = 1L;
		String movieId = "20223819";
		mockMvc.perform(get("/ticket/screen")
				.param("cinemaId", String.valueOf(cinemaId))
				.param("movieId", movieId))
				.andExpect(status().isOk());
		
	}

	@Test
	@DisplayName("좌석 정보 조회 테스트")
	void testGetSeatList() throws Exception{
		Long playingId = 15L;
		mockMvc.perform(get("/ticket/seats")
				.param("playingId", String.valueOf(playingId)))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("티켓 정보 조회 테스트")
	void testGetSeatInfo() throws Exception{
		TicketRequestDto ticket = new TicketRequestDto();
		ticket.setPlayingId(15L);
		ticket.setMovieId("20223819");
		ticket.setScreenId(1L);
		ticket.setSeatName("E6");
		
		mockMvc.perform(get("/ticket/seats/info")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ticket)))
		.andExpect(status().isOk());
	}

	@Test
	@DisplayName("상영정보 조회 테스트")
	void testGetPlayingInfo() throws Exception {
	    // given

        // when & then
        mockMvc.perform(get("/ticket/playing/1")
        		.cookie(new Cookie("jwt", token)))
               .andExpect(status().isOk());
	}

}
