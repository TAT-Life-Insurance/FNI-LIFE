package org.ace.insurance.managementreport.policy.persistence.interfaces;

import org.ace.insurance.managementreport.policy.ActivePolicies;

public interface IActivePoliciesDAO {
	public ActivePolicies findActivePoliciesByProducts();
	public ActivePolicies findTotalSumInsuredByProducts();
	public ActivePolicies findTotalPremiumByProducts();
	public ActivePolicies findLifePolicyByTimeLine(int year);
	public ActivePolicies findFirePolicyByTimeLine(int year);
	public ActivePolicies findMotorPolicyByTimeLine(int year);
}
