/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.common;

public enum InsuranceType {

	LIFE("Life"),

	HEALTH("Health"),

	PERSON_TRAVEL("Preson Travel"),

	FARMER("Farmer"),

	SHORT_ENDOWMENT_LIFE("Short Endowment Life"),

	SPORTMAN("Sport Man"),

	SPORTMANABROAD("Sport Man Abroad");

	private String label;

	private InsuranceType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}