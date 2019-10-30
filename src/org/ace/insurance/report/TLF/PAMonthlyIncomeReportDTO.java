package org.ace.insurance.report.TLF;

import java.util.Date;

public class PAMonthlyIncomeReportDTO {
	private String customerName;
	private String address;
	private double sumInsured;
	private double premium;
	private String agentName;
	private String receiptNo;
	private String policyNo;
	private Date paymentDate;
	private String liscenseNo;
	private int periodMonth;
	private Date activedPolicyStartDate;
	private Date activedPolicyEndDate;
	private double commission;
	private int numberOfInsuredPerson;
	private String branchName;
	private double siUsd;
	private double premiumUsd;
	private double comissionUsd;

	public PAMonthlyIncomeReportDTO(String customerName, String address, double sumInsured, double premium, String agentName, String receiptNo, Date paymentDate, String liscenseNo,
			int periodMonth, Date activedPolicyStartDate, Date activedPolicyEndDate, double commission, int numberOfInsuredPerson, String branchName, String policyNo, double siUsd,
			double premiumUsd, double comissionUsd) {
		super();
		this.customerName = customerName;
		this.address = address;
		this.sumInsured = sumInsured;
		this.premium = premium;
		this.agentName = agentName;
		this.receiptNo = receiptNo;
		this.paymentDate = paymentDate;
		this.liscenseNo = liscenseNo;
		this.periodMonth = periodMonth;
		this.activedPolicyStartDate = activedPolicyStartDate;
		this.activedPolicyEndDate = activedPolicyEndDate;
		this.commission = commission;
		this.numberOfInsuredPerson = numberOfInsuredPerson;
		this.branchName = branchName;
		this.policyNo = policyNo;
		this.siUsd = siUsd;
		this.premiumUsd = premiumUsd;
		this.comissionUsd = comissionUsd;
	}

	public String getBranchName() {
		return branchName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getAddress() {
		return address;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public double getPremium() {
		return premium;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getLiscenseNo() {
		return liscenseNo;
	}

	public int getPeriodMonth() {
		return periodMonth;
	}

	public Date getActivedPolicyStartDate() {
		return activedPolicyStartDate;
	}

	public Date getActivedPolicyEndDate() {
		return activedPolicyEndDate;
	}

	public double getCommission() {
		return commission;
	}

	public int getNumberOfInsuredPerson() {
		return numberOfInsuredPerson;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public double getSiUsd() {
		return siUsd;
	}

	public double getPremiumUsd() {
		return premiumUsd;
	}

	public double getComissionUsd() {
		return comissionUsd;
	}

	public double getSiWithAllCur() {
		if (sumInsured == 0.0)
			return sumInsured = siUsd;
		else
			return sumInsured;
	}

	public double getPremiumWithAllCur() {
		if (premium == 0.0)
			return premium = premiumUsd;
		else
			return premium;
	}

	public double getComissionWithAllCur() {
		if (commission == 0.0)
			return commission = comissionUsd;
		else
			return commission;
	}

}
