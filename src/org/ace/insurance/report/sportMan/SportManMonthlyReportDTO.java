package org.ace.insurance.report.sportMan;

import java.util.Date;

public class SportManMonthlyReportDTO {

	private double amount;
	private double commission;
	private double sumInsured;
	private String receiptNo;
	private String policyNo;
	private String insuredPersonName;
	private String agentName;
	private String liscenseNo;
	private String salesPointsId;
	private String branchId;
	private String residentAddress;
	private String typeOfSport;
	private Date paymentDate;

	public SportManMonthlyReportDTO(String insuredPerson, String address, String district, String province, String policyNo, String typeOfSport, double suminsured, double premium,
			String receiptNo, Date paymentDate, double commission, String agentName, String liscenseNo) {
		this.insuredPersonName = insuredPerson;
		this.residentAddress = address + "," + district + "," + province;
		this.policyNo = policyNo;
		this.typeOfSport = typeOfSport;
		this.sumInsured = suminsured;
		this.amount = premium;
		this.receiptNo = receiptNo;
		this.paymentDate = paymentDate;
		this.commission = commission;
		this.agentName = agentName;
		this.liscenseNo = liscenseNo;

	}

	public double getAmount() {
		return amount;
	}

	public double getCommission() {
		return commission;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getInsuredPersonName() {
		return insuredPersonName;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getLiscenseNo() {
		return liscenseNo;
	}

	public String getSalesPointsId() {
		return salesPointsId;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getResidentAddress() {
		return residentAddress;
	}

	public String getTypeOfSport() {
		return typeOfSport;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

}
