package org.ace.insurance.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.ace.insurance.system.common.township.Township;

@Embeddable
public class ClaimInitialReporter implements Serializable {
	private static final long serialVersionUID = -2074848703209463245L;

	@Column(name = "REPORTER_NAME")
	private String name;

	@Column(name = "REPORTER_IDTYPE")
	@Enumerated(value = EnumType.STRING)
	private IdType idType;

	// @OneToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "REPORTER_STATECODEID", referencedColumnName = "ID")
	// private StateCode stateCode;
	//
	// @OneToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "REPORTER_TOWNSHIPCODEID", referencedColumnName =
	// "ID")
	// private TownshipCode townshipCode;

	@Column(name = "IDCONDITIONTYPE")
	private IdConditionType idConditionType;

	@Column(name = "FULLIDNO")
	private String fullIdNo;

	@Column(name = "REPORTER_IDNO")
	private String idNo;

	@Column(name = "REPORTER_RESIDENTADDRESS")
	private String residentAddress;

	@Column(name = "REPORTER_PHONE")
	private String phone;

	@Column(name = "REPORTER_FATHERNAME")
	private String fatherName;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REPORTER_TOWNSHIPID", referencedColumnName = "ID")
	private Township township;

	public ClaimInitialReporter() {

	}

	public ClaimInitialReporter(String name, IdType idType,
			/* StateCode stateCode, TownshipCode townshipCode, */ IdConditionType idConditionType, String idNo, String residentAddress, String phone, String fatherName,
			Township township) {
		super();
		this.name = name;
		this.idType = idType;
		// this.stateCode = stateCode;
		// this.townshipCode = townshipCode;
		this.idConditionType = idConditionType;
		this.idNo = idNo;
		this.residentAddress = residentAddress;
		this.phone = phone;
		this.fatherName = fatherName;
		this.township = township;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getResidentAddress() {
		return residentAddress;
	}

	public void setResidentAddress(String residentAddress) {
		this.residentAddress = residentAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public Township getTownship() {
		return township;
	}

	public void setTownship(Township township) {
		this.township = township;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fatherName == null) ? 0 : fatherName.hashCode());
		result = prime * result + ((idNo == null) ? 0 : idNo.hashCode());
		result = prime * result + ((idType == null) ? 0 : idType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((residentAddress == null) ? 0 : residentAddress.hashCode());
		result = prime * result + ((township == null) ? 0 : township.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClaimInitialReporter other = (ClaimInitialReporter) obj;
		if (fatherName == null) {
			if (other.fatherName != null)
				return false;
		} else if (!fatherName.equals(other.fatherName))
			return false;
		if (idNo == null) {
			if (other.idNo != null)
				return false;
		} else if (!idNo.equals(other.idNo))
			return false;
		if (idType != other.idType)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (residentAddress == null) {
			if (other.residentAddress != null)
				return false;
		} else if (!residentAddress.equals(other.residentAddress))
			return false;
		if (township == null) {
			if (other.township != null)
				return false;
		} else if (!township.equals(other.township))
			return false;
		return true;
	}

	// public StateCode getStateCode() {
	// return stateCode;
	// }
	//
	// public void setStateCode(StateCode stateCode) {
	// this.stateCode = stateCode;
	// }
	//
	// public TownshipCode getTownshipCode() {
	// return townshipCode;
	// }
	//
	// public void setTownshipCode(TownshipCode townshipCode) {
	// this.townshipCode = townshipCode;
	// }

	public IdConditionType getIdConditionType() {
		return idConditionType;
	}

	public void setIdConditionType(IdConditionType idConditionType) {
		this.idConditionType = idConditionType;
	}

	public String getFullIdNo() {
		return fullIdNo;
	}

	public void setFullIdNo(String fullIdNo) {
		this.fullIdNo = fullIdNo;
	}
}
