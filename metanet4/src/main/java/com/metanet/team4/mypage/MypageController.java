package com.metanet.team4.mypage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.team4.mypage.model.MypageMember;
import com.metanet.team4.mypage.model.MypageResponse;
import com.metanet.team4.mypage.model.ReserveList;
import com.metanet.team4.mypage.service.IMypageService;

import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/mypage")
@Tag(name="Mypage", description="mypage")
public class MypageController {

	@Autowired
	IMypageService mypageService;
	
	@GetMapping("")
	public MypageResponse getMypageContent() {
		String memberId = "aaa";
		MypageMember mypageMember = mypageService.getMypageMember(memberId);
		List<ReserveList> reserveList = mypageService.getReserveList(memberId);
		List<ReserveList> cancelList = mypageService.getCancelList(memberId);
	    return new MypageResponse(mypageMember, reserveList, cancelList);
	}
}
