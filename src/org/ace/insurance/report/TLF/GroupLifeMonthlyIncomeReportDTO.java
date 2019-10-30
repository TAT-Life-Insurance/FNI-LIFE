package org.ace.insurance.report.TLF;

import java.util.Date;

import org.ace.insurance.common.Utils;

public class GroupLifeMonthlyIncomeReportDTO {

	private String policyNo;
	private String receiptNo;
	private String customerName;
	private String agentName;
	private Date paymentDate;
	private double premium;
	private double commission;
	private double sumInsured;
	private int insuPersonCount;
	private String address;

	public GroupLifeMonthlyIncomeReportDTO(String policyNo, String receiptNo, String customerName, String agentName, Date paymentDate, double sumInsured, double premium,
			double commission, int insuPersonCount, String address) {
		this.policyNo = policyNo;
		this.receiptNo = receiptNo;
		this.customerName = customerName;
		this.agentName = agentName;
		this.paymentDate = paymentDate;
		this.sumInsured = sumInsured;
		this.premium = premium;
		this.commission = commission;
		this.insuPersonCount = insuPersonCount;
		this.address = address;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getAgentName() {
		return agentName;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public double getPremium() {
		return premium;
	}

	public double getCommission() {
		return commission;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public int getInsuPersonCount() {
		return insuPersonCount;
	}

	public String getAddress() {
		return address;
	}

	public String getReceiptNoAndDate() {
		return getReceiptNo() + " (" + Utils.getDateFormatString(getPaymentDate()) +")";
	}

}
