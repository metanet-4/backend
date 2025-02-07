package com.metanet.team4.mypage.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.metanet.team4.mypage.model.MypageMember;
import com.metanet.team4.mypage.model.ReserveList;

@Repository
public interface IMypageRepository {
	MypageMember getMypageMember(String memberId);
	List<ReserveList> getReserveList(String memberId);
	List<ReserveList> getCancelList(String memberId);
}
