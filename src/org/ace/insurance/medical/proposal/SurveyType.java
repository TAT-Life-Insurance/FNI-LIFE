package org.ace.insurance.medical.proposal;

public enum SurveyType {
	MEDICAL_UNDERWRITING_SURVEY("medical_underwriting_survey"),

	MEDICAL_CLAIM_SURVEY("medical_Claim_survey"),

	SHORT_ENDOWMENT_LIFE_SURVEY("short_endowment_life_underwriting_survey"),

	FIRE_UNDERWRITING_SURVEY("fire_underwriting_survey");

	private String label;

	private SurveyType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
