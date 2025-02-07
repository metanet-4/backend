package com.metanet.team4.mypage.service;

import java.util.List;

import com.metanet.team4.mypage.model.MypageMember;
import com.metanet.team4.mypage.model.ReserveList;

public interface IMypageService {
	MypageMember getMypageMember(String memberId);
	List<ReserveList> getReserveList(String memberId);
	List<ReserveList> getCancelList(String memberId);
}
