package com.ulegalize.lawfirm.model;

import lombok.Data;

import java.util.List;

@Data
public class LawyerDTO extends AbstractRestObject {

	private Long id;
	private String alias;
	private String fullName;
	private String initials;
	private String summary;
	private String biography;
	private String specialities;
	private String color;
	private byte[] picture;
	private String email;
	private String phone;
	private List<String> vcKeySelected;

	private List<LawyerDuty> duties;


}
