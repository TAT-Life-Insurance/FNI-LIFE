package org.ace.insurance.life.endorsement;

public enum InsuredPersonEndorseStatus {

	REPLACE("Replace"), 
	NEW("New"),
	EDIT("Edit");
	
	private String label;

	private InsuredPersonEndorseStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
