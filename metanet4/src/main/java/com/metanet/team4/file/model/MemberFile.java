package com.metanet.team4.file.model;

import lombok.Data;

@Data
public class MemberFile {
	private Long id;
	private byte[] image;
	private byte[] disabilityCertificate;
}
