package org.ace.insurance.report.TLF;

import java.util.Date;

public class SnakeBiteMonthlyIncomeReportDTO {

	private String policyNo;
	private String customerName;
	private String agentName;
	private Date paymentDate;
	private double premium;
	private double commission;
	private double sumInsured;
	private int unit;
	private String address;
	private String idNo;

	public SnakeBiteMonthlyIncomeReportDTO(String policyNo, String customerName, String agentName, Date paymentDate, double sumInsured, double premium,double commission, 
			int unit, String address, String idNo) {
		this.policyNo = policyNo;
		this.idNo = idNo;
		this.customerName = customerName;
		this.agentName = agentName;
		this.paymentDate = paymentDate;
		this.sumInsured = sumInsured;
		this.premium = premium;
		this.commission = commission;
		this.unit = unit;
		this.address = address;
	}

	public String getPolicyNo() {
		return policyNo;
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

	public int getUnit() {
		return unit;
	}

	public String getAddress() {
		return address;
	}

	public String getIdNo() {
		return idNo;
	}

}
