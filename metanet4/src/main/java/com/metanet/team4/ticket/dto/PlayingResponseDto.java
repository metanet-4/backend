package com.metanet.team4.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayingResponseDto {
    private String krName;       // 영화 제목
    private String watchGrade;   // 연령 제한
    private String playingTime;  // 상영 시간 (날짜 + 시작시간 ~ 종료시간)
    private String cinemaName;   // 영화관 이름
    private String screenName;   // 상영관 이름
}
