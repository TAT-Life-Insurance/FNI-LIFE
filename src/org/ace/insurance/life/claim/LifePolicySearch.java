package org.ace.insurance.life.claim;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;

public class LifePolicySearch {
	private String policyNo;
	private Customer customer;
	private Organization organization;
	private Branch branch;
	private String nrcNo;
	private String father;

	public LifePolicySearch() {
	}

	public LifePolicySearch(Object object) {
		Object[] objArray = (Object[]) object;
		if (objArray[0] instanceof String) {
			policyNo = (String) objArray[0];
		}
		if (objArray[1] instanceof Customer) {
			customer = (Customer) objArray[1];
		}
		if (objArray[2] instanceof Organization) {
			organization = (Organization) objArray[2];
		}
		if (objArray[3] instanceof Branch) {
			branch = (Branch) objArray[3];
		}
		if (objArray[4] instanceof String) {
			nrcNo = (String) objArray[4];
		}
		if (objArray[5] instanceof String) {
			father = (String) objArray[5];
		}

	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getNrcNo() {
		return nrcNo;
	}

	public void setNrcNo(String nrcNo) {
		this.nrcNo = nrcNo;
	}

	public String getFather() {
		return father;
	}

	public void setFather(String father) {
		this.father = father;
	}

	public String getCustomerName() {
		if (customer != null) {
			return customer.getFullName();
		}
		if (organization != null) {
			return organization.getName();
		}
		return null;
	}
}
