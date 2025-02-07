package com.metanet.team4.mypage.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MypageResponse {
    private MypageMember mypageMember;
    private List<ReserveList> reserveList;
    private List<ReserveList> cancelList;
    
}
