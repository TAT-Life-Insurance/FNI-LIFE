package org.ace.insurance.life.claim;

import java.util.Date;

import org.ace.insurance.common.ISorter;

public class LCL001 implements ISorter {
	private String id;
	private String policyId;
	private String policyNo;
	private String insuredPerson;
	private double sumInsured;
	private String paymentType;
	private Date submittedDate;
	private String claimRole;
	private Date claimDate;
	
	public LCL001(String id, String policyNo, String claimRole, String insuredPerson,
				  double sumInsured, String paymenttype, Date submittedDate) {
		this.id = id;
		this.policyNo = policyNo;
		this.claimRole = claimRole;
		this.insuredPerson = insuredPerson;
		this.sumInsured = sumInsured;
		this.paymentType = paymenttype;
		this.submittedDate = submittedDate;
	}
	
	public LCL001() {		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getInsuredPerson() {
		return insuredPerson;
	}

	public void setInsuredPerson(String insuredPerson) {
		this.insuredPerson = insuredPerson;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getClaimRole() {
		return claimRole;
	}

	public void setClaimRole(String claimRole) {
		this.claimRole = claimRole;
	}

	public Date getClaimDate() {
		return claimDate;
	}

	public void setClaimDate(Date claimDate) {
		this.claimDate = claimDate;
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}	
	
}
