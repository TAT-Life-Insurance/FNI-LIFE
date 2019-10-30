package org.ace.insurance.report.TLF;

import java.util.Date;

public class MonthlyIncomeReportCriteria {

	private String salePointId;
	private String salePointName;
	private String saleChannelType;
	private Date startDate;
	private Date endDate;
	private String branchName;
	private String branchId;
	private boolean isIncludeAgent;

	public String getSalePointName() {
		return salePointName;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
		if (salePointName == null || salePointName.isEmpty()) {
			this.salePointName = null;
			this.salePointId = null;
		}
	}

	public String getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(String saleChannelType) {
		this.saleChannelType = saleChannelType;
	}

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

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public boolean isIncludeAgent() {
		return isIncludeAgent;
	}

	public void setIncludeAgent(boolean isIncludeAgent) {
		this.isIncludeAgent = isIncludeAgent;
	}
}
