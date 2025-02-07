package com.metanet.team4.mypage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metanet.team4.mypage.dao.IMypageRepository;
import com.metanet.team4.mypage.model.MypageMember;
import com.metanet.team4.mypage.model.ReserveList;

@Service
public class MypageService implements IMypageService {

	@Autowired
	IMypageRepository mypageRepository;

	@Override
	public MypageMember getMypageMember(String memberId) {
		return mypageRepository.getMypageMember(memberId);
	}

	@Override
	public List<ReserveList> getReserveList(String memberId) {
		return mypageRepository.getReserveList(memberId);
	}

	@Override
	public List<ReserveList> getCancelList(String memberId) {
		return mypageRepository.getCancelList(memberId);
	}

}
