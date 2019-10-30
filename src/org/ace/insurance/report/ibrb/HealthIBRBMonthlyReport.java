package org.ace.insurance.report.ibrb;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ace.insurance.common.CustomerType;
import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.VWT_FNI_HEALTH_IBRB_MONTHLY_REPORT)
@ReadOnly
public class HealthIBRBMonthlyReport {
	@Id
	private String id;
	private String insuredPersonName;
	private String branchId;
	private String salePointId;
	private Date activedPolicyStartDate;
	private Date paymentDate;
	private String receiptNo;
	private String policyNo;
	private String gender;
	private int age;
	private String occupation;
	private String residentAddress;
	private String province;
	private String township;
	private String paymentType;
	@Enumerated(EnumType.STRING)
	private CustomerType customerType;
	private double totalPremium;
	private String benefInfo;
	private String hisInfo;
	private String hisDis;
	private int basicUnit;
	private int addOn1Unit;
	private int addOn2Unit;

	public HealthIBRBMonthlyReport() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInsuredPersonName() {
		return insuredPersonName;
	}

	public void setInsuredPersonName(String insuredPersonName) {
		this.insuredPersonName = insuredPersonName;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Date getActivedPolicyStartDate() {
		return activedPolicyStartDate;
	}

	public void setActivedPolicyStartDate(Date activedPolicyStartDate) {
		this.activedPolicyStartDate = activedPolicyStartDate;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getResidentAddress() {
		return residentAddress;
	}

	public void setResidentAddress(String residentAddress) {
		this.residentAddress = residentAddress;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getTownship() {
		return township;
	}

	public void setTownship(String township) {
		this.township = township;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public CustomerType getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

	public double getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(double totalPremium) {
		this.totalPremium = totalPremium;
	}

	public String getBenefInfo() {
		return benefInfo;
	}

	public void setBenefInfo(String benefInfo) {
		this.benefInfo = benefInfo;
	}

	public String getHisInfo() {
		return hisInfo;
	}

	public void setHisInfo(String hisInfo) {
		this.hisInfo = hisInfo;
	}

	public String getHisDis() {
		return hisDis;
	}

	public void setHisDis(String hisDis) {
		this.hisDis = hisDis;
	}

	public int getBasicUnit() {
		return basicUnit;
	}

	public void setBasicUnit(int basicUnit) {
		this.basicUnit = basicUnit;
	}

	public int getAddOn1Unit() {
		return addOn1Unit;
	}

	public void setAddOn1Unit(int addOn1Unit) {
		this.addOn1Unit = addOn1Unit;
	}

	public int getAddOn2Unit() {
		return addOn2Unit;
	}

	public void setAddOn2Unit(int addOn2Unit) {
		this.addOn2Unit = addOn2Unit;
	}

}
