package org.ace.insurance.report.TLF;

import java.util.Date;

public class SnakeBiteMonthlyIncomeReportCriteria {

	private Date startDate;
	private Date endDate;
	private String branchId;
	private String branchName;
	private String salePointId;
	private String salePointName;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
		if (salePointName == null || salePointName.isEmpty()) {
			this.salePointName = null;
			this.salePointId = null;
		}
	}

}
