package com.metanet.team4.mypage.model;

import java.util.List;

import com.metanet.team4.movie.model.Movie;
import com.metanet.team4.movie.model.MovieMemberForChart;

import lombok.Data;

@Data
public class MypageResponse {
    private MypageMember mypageMember;
    private List<ReserveList> reserveList;
    private List<ReserveList> cancelList;
    public MypageResponse(MypageMember mypageMember, List<ReserveList> reserveList, List<ReserveList> cancelList) {
        this.mypageMember=mypageMember;
        this.reserveList=reserveList;
        this.cancelList=cancelList;
    }
}
